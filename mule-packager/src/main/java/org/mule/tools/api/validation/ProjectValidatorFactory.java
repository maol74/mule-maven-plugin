/*
 * Mule ESB Maven Tools
 * <p>
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * <p>
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.tools.api.validation;

import org.mule.tools.api.packager.ProjectInformation;

import org.mule.maven.client.internal.AetherMavenClient;
import org.mule.tools.api.classloader.model.SharedLibraryDependency;
import org.mule.tools.api.packager.packaging.PackagingType;
import org.mule.tools.api.util.Project;

import java.util.List;

import static org.mule.tools.api.packager.packaging.PackagingType.MULE_DOMAIN_BUNDLE;

public class ProjectValidatorFactory {

  public static AbstractProjectValidator create(ProjectInformation info, Project dependencyProject,
                                                MulePluginResolver resolver, AetherMavenClient aetherMavenClient,
                                                List<SharedLibraryDependency> sharedLibraries) {
    if (PackagingType.fromString(info.getPackaging()).equals(MULE_DOMAIN_BUNDLE)) {
      return new DomainBundleProjectValidator(info.getProjectBaseFolder(), dependencyProject, resolver,
                                              aetherMavenClient);
    }
    return new MuleProjectValidator(info.getProjectBaseFolder(), info.getPackaging(), dependencyProject, resolver,
                                    sharedLibraries);
  }
}