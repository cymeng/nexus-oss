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
package org.sonatype.nexus.tasks;

import java.io.IOException;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.nexus.feeds.FeedRecorder;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.wastebasket.RepositoryFolderRemover;
import org.sonatype.nexus.scheduling.AbstractNexusRepositoriesTask;
import org.sonatype.scheduling.SchedulerTask;

/**
 * Delete repository folders
 * 
 * @author Juven Xu
 */
@Component( role = SchedulerTask.class, hint = "DeleteRepositoryFoldersTask", instantiationStrategy = "per-lookup" )
public class DeleteRepositoryFoldersTask
    extends AbstractNexusRepositoriesTask<Object>
{
    @Requirement
    private RepositoryFolderRemover repositoryFolderRemover;

    private boolean deleteForever = false;

    private Repository repository = null;

    public void setRepository( Repository repository )
    {
        this.repository = repository;
        setRepositoryId( repository.getId() );
    }

    public boolean isDeleteForever()
    {
        return deleteForever;
    }

    public void setDeleteForever( boolean deleteForever )
    {
        this.deleteForever = deleteForever;
    }

    @Override
    public boolean isExposed()
    {
        return false;
    }

    @Override
    protected Object doRun()
        throws Exception
    {
        if ( repository != null )
        {
            try
            {
                repositoryFolderRemover.deleteRepositoryFolders( repository, deleteForever );
            }
            catch ( IOException e )
            {
                getLogger().warn( "Unable to delete repository folders ", e );
            }
        }

        return null;
    }

    @Override
    protected String getAction()
    {
        return FeedRecorder.SYSTEM_REMOVE_REPO_FOLDER_ACTION;
    }

    @Override
    protected String getMessage()
    {
        if ( getRepositoryId() != null )
        {
            return "Deleting folders with repository ID: " + getRepositoryId();
        }
        return null;
    }

    @Override
    public String getRepositoryName()
    {
        return repository.getName();
    }

}
