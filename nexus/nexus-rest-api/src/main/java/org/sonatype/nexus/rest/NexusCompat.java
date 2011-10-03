/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/oss/attributions
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.rest;

import org.sonatype.nexus.configuration.model.CRepository;
import org.sonatype.nexus.configuration.model.CRepositoryCoreConfiguration;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.nexus.proxy.repository.Repository;

public class NexusCompat
{
    public static CRepository getRepositoryRawConfiguration( Repository repository )
    {
        return ( (CRepositoryCoreConfiguration) repository.getCurrentCoreConfiguration() ).getConfiguration( false );
    }

    /**
     * Returns repository's role.
     * 
     * @param repository
     * @return
     * @deprecated Use repository.getProviderRole() instead!
     */
    public static String getRepositoryProviderRole( Repository repository )
    {
        return repository.getProviderRole();
    }

    /**
     * Returns repository's hint.
     * 
     * @param repository
     * @return
     * @deprecated Use Repository.getProviderHint() instead!
     */
    public static String getRepositoryProviderHint( Repository repository )
    {
        return repository.getProviderHint();
    }

    public static String getRepositoryPolicy( Repository repository )
    {
        if ( repository.getRepositoryKind().isFacetAvailable( MavenRepository.class ) )
        {
            return repository.adaptToFacet( MavenRepository.class ).getRepositoryPolicy().toString();
        }
        else
        {
            return null;
        }
    }
}
