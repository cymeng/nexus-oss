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
package org.sonatype.nexus.plugins.mavenbridge.workspace;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.maven.index.artifact.Gav;
import org.codehaus.plexus.util.StringUtils;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.WorkspaceReader;
import org.sonatype.aether.repository.WorkspaceRepository;
import org.sonatype.nexus.proxy.item.StorageFileItem;
import org.sonatype.nexus.proxy.maven.ArtifactStoreRequest;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.nexus.proxy.storage.local.fs.DefaultFSLocalRepositoryStorage;

public class NexusWorkspaceReader
    implements WorkspaceReader
{
    private final NexusWorkspace nexusWorkspace;

    private final WorkspaceRepository workspaceRepository;

    public NexusWorkspaceReader( NexusWorkspace nexusWorkspace )
    {
        this.nexusWorkspace = nexusWorkspace;

        this.workspaceRepository = new WorkspaceRepository( "nexus", nexusWorkspace.getId() );
    }

    public WorkspaceRepository getRepository()
    {
        return workspaceRepository;
    }

    /**
     * This method will in case of released artifact request just locate it, and return if found. In case of snapshot
     * repository, if it needs resolving, will resolve it 1st and than locate it. It will obey to the session (global
     * update policy, that correspondos to Maven CLI "-U" option.
     */
    public File findArtifact( Artifact artifact )
    {
        Gav gav = toGav( artifact );

        ArtifactStoreRequest gavRequest;

        for ( MavenRepository mavenRepository : nexusWorkspace.getRepositories() )
        {
            gavRequest = new ArtifactStoreRequest( mavenRepository, gav, false, false );

            try
            {
                StorageFileItem artifactFile = mavenRepository.getArtifactStoreHelper().retrieveArtifact( gavRequest );

                // this will work with local FS storage only, since Aether wants java.io.File
                if ( artifactFile.getRepositoryItemUid().getRepository().getLocalStorage() instanceof DefaultFSLocalRepositoryStorage )
                {
                    DefaultFSLocalRepositoryStorage ls =
                        (DefaultFSLocalRepositoryStorage) artifactFile.getRepositoryItemUid().getRepository().getLocalStorage();

                    return ls.getFileFromBase( artifactFile.getRepositoryItemUid().getRepository(), gavRequest );
                }
            }
            catch ( Exception e )
            {
                // Something wrong happen for this repository, let's process the next one
            }
        }

        return null;
    }

    private Gav toGav( Artifact artifact )
    {
        // fix for bug in M2GavCalculator
        final String classifier = StringUtils.isEmpty( artifact.getClassifier() ) ? null : artifact.getClassifier();

        final Gav gav =
            new Gav( artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), classifier,
                artifact.getExtension(), null, null, null, false, null, false, null );

        return gav;
    }

    /**
     * Basically, this method will read the GA metadata, and return the "known versions".
     */
    public List<String> findVersions( Artifact artifact )
    {
        Gav gav = toGav( artifact );

        if ( gav.isSnapshot() )
        {
            ArtifactStoreRequest gavRequest;

            for ( MavenRepository mavenRepository : nexusWorkspace.getRepositories() )
            {
                gavRequest = new ArtifactStoreRequest( mavenRepository, gav, false, false );

                try
                {
                    Gav snapshot = mavenRepository.getMetadataManager().resolveSnapshot( gavRequest, gav );
                    return Collections.singletonList( snapshot.getVersion() );
                }
                catch ( Exception e )
                {
                    // try next repo
                    continue;
                }
            }
        }

        return null;
    }

}
