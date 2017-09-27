/*
 * Mule ESB Maven Tools
 * <p>
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * <p>
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.tools.api.classloader.model;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.io.File.separatorChar;
import static org.apache.commons.io.FilenameUtils.*;

public class Artifact implements Comparable {

  private ArtifactCoordinates artifactCoordinates;
  private URI uri;

  public Artifact(ArtifactCoordinates artifactCoordinates, URI uri) {
    setArtifactCoordinates(artifactCoordinates);
    setUri(uri);
  }

  public ArtifactCoordinates getArtifactCoordinates() {
    return artifactCoordinates;
  }

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    checkNotNull(uri, "Uri cannot be null");
    this.uri = uri;
  }

  private void setArtifactCoordinates(ArtifactCoordinates artifactCoordinates) {
    checkNotNull(artifactCoordinates, "Artifact coordinates cannot be null");
    this.artifactCoordinates = artifactCoordinates;
  }

  @Override
  public String toString() {
    return artifactCoordinates.toString();
  }

  @Override
  public int compareTo(Object that) {
    return this.getSimplifiedMavenCoordinates().compareTo(((Artifact) that).getSimplifiedMavenCoordinates());
  }

  protected String getSimplifiedMavenCoordinates() {
    ArtifactCoordinates coordinates = this.getArtifactCoordinates();
    return coordinates.getGroupId() + ":" + coordinates.getArtifactId() + ":" + coordinates.getVersion();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Artifact that = (Artifact) o;

    return getArtifactCoordinates().equals(that.getArtifactCoordinates());
  }

  @Override
  public int hashCode() {
    return getArtifactCoordinates().hashCode();
  }

  public Artifact copyWithParameterizedUri() {
    Artifact newArtifact = new Artifact(artifactCoordinates, uri);
    File repositoryFolder = new File("repository");
    String artifactFilename = getFormattedArtifactFileName(newArtifact);
    String newUriPath = getFormattedMavenDirectory(repositoryFolder, this.getArtifactCoordinates()).getPath();
    File newArtifactFile = new File(newUriPath, artifactFilename);
    try {
      setNewArtifactURI(newArtifact, newArtifactFile);
    } catch (URISyntaxException e) {
      throw new RuntimeException("Could not generate URI for resource, the given path is invalid: " + newUriPath, e);
    }
    return newArtifact;
  }

  /**
   * Resolves an artifact full path in a given repository based on the default maven repository layout.
   *
   * @param outputDirectory the directory that is going to have its path prepended to the formatted output directory.
   * @param artifactCoordinates the artifact coordinates that from which the formatted output directory is going to be
   *        constructed.
   * @return the formatted artifact path.
   */
  public static File getFormattedMavenDirectory(File outputDirectory, ArtifactCoordinates artifactCoordinates) {
    StringBuilder sb = new StringBuilder();

    sb.append(artifactCoordinates.getGroupId().replace('.', separatorChar)).append(separatorChar);
    sb.append(artifactCoordinates.getArtifactId()).append(separatorChar);
    sb.append(artifactCoordinates.getVersion()).append(separatorChar);

    return new File(outputDirectory, sb.toString());
  }

  /**
   * Build the default artifact file name in a maven repository.
   *
   * @param artifact the artifact from which the default file name is going to be resolved.
   * @return the default artifact file name that a repository resource contains given its type.
   */
  public static String getFormattedArtifactFileName(Artifact artifact) {
    ArtifactCoordinates artifactCoordinates = artifact.getArtifactCoordinates();
    String destFileName = buildMainFileName(artifactCoordinates);
    String extension = artifactCoordinates.getType();
    return destFileName + "." + extension;
  }

  /**
   * Build the main main artifact file name in a maven repository.
   *
   * @param artifactCoordinates the artifact coordinates from which the main file name is going to be resolved.
   * @return the main artifact file name that a repository resource contains without its type extension.
   */
  private static String buildMainFileName(ArtifactCoordinates artifactCoordinates) {
    String versionString = "-" + artifactCoordinates.getVersion();
    String classifierString = StringUtils.EMPTY;
    if (StringUtils.isNotBlank(artifactCoordinates.getClassifier())) {
      classifierString = "-" + artifactCoordinates.getClassifier();
    }
    return artifactCoordinates.getArtifactId() + versionString + classifierString;
  }

  public void setNewArtifactURI(Artifact newArtifact, File newArtifactFile) throws URISyntaxException {
    String relativePath = normalize(newArtifactFile.getPath(), true);
    newArtifact.setUri(new URI(relativePath));
  }

  /**
   * Build the default pom file name in a maven repository.
   *
   * @param artifact the artifact from which the default pom file name is going to be resolved.
   * @return the default pom file name that a repository resource contains given an artifact.
   */
  public static String getPomFileName(Artifact artifact) {
    return buildMainPOMFileName(artifact.getArtifactCoordinates()) + ".pom";
  }

  /**
   * Build the main pom file name in a maven repository.
   *
   * @param artifactCoordinates the artifact coordinates from which the main pom file name is going to be resolved.
   * @return the main pom file name that a repository resource contains without its pom extension.
   */
  public static String buildMainPOMFileName(ArtifactCoordinates artifactCoordinates) {
    String versionString = "-" + artifactCoordinates.getVersion();
    return artifactCoordinates.getArtifactId() + versionString;
  }

  /**
   * Build the default pom file name in a maven repository.
   *
   * @return the default pom file name that a repository resource contains given an artifact.
   */
  public String getPomFileName() {
    return getPomFileName(this);
  }

  public String getFormattedArtifactFileName() {
    return getFormattedArtifactFileName(this);
  }

  public File getFormattedMavenDirectory(File repositoryFile) {
    return getFormattedMavenDirectory(repositoryFile, artifactCoordinates);
  }
}