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
package org.sonatype.nexus.integrationtests.nexus1923;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.index.context.IndexingContext;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.FileUtils;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.proxy.maven.ChecksumPolicy;
import org.sonatype.nexus.proxy.maven.RepositoryPolicy;
import org.sonatype.nexus.proxy.repository.RepositoryWritePolicy;
import org.sonatype.nexus.rest.model.NexusArtifact;
import org.sonatype.nexus.rest.model.RepositoryGroupMemberRepository;
import org.sonatype.nexus.rest.model.RepositoryGroupResource;
import org.sonatype.nexus.rest.model.RepositoryResource;
import org.sonatype.nexus.rest.model.RepositoryResourceRemoteStorage;
import org.sonatype.nexus.rest.model.ScheduledServiceBaseResource;
import org.sonatype.nexus.rest.model.ScheduledServicePropertyResource;
import org.sonatype.nexus.tasks.descriptors.UpdateIndexTaskDescriptor;
import org.sonatype.nexus.test.utils.GroupMessageUtil;
import org.sonatype.nexus.test.utils.RepositoryMessageUtil;
import org.sonatype.nexus.test.utils.TaskScheduleUtil;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

public abstract class AbstractNexus1923
    extends AbstractNexusIntegrationTest
{
    protected RepositoryMessageUtil repoUtils;

    protected GroupMessageUtil groupUtils;

    protected static final String HOSTED_REPO_ID = "incremental_repo";

    protected static final String SECOND_HOSTED_REPO_ID = "incremental_repo_second";

    protected static final String THIRD_HOSTED_REPO_ID = "incremental_repo_third";

    protected static final String PROXY_REPO_ID = "incremental_repo_proxy";

    protected static final String GROUP_ID = "index_group";

    protected static final String FIRST_ARTIFACT = "firstArtifact";

    protected static final String SECOND_ARTIFACT = "secondArtifact";

    protected static final String THIRD_ARTIFACT = "thirdArtifact";

    protected static final String FOURTH_ARTIFACT = "fourthArtifact";

    protected static final String FIFTH_ARTIFACT = "fifthArtifact";

    protected static final String HOSTED_REINDEX_TASK_NAME = "incremental_reindex";

    protected static final String SECOND_HOSTED_REINDEX_TASK_NAME = "incremental_reindex_second";

    protected static final String PROXY_REINDEX_TASK_NAME = "incremental_reindex_proxy";

    protected static final String GROUP_REINDEX_TASK_NAME = "incremental_reindex_group";

    public AbstractNexus1923()
    {
        super();
    }

    @BeforeClass( alwaysRun = true )
    public void init()
        throws ComponentLookupException
    {
        this.repoUtils = new RepositoryMessageUtil( this, this.getJsonXStream(), MediaType.APPLICATION_JSON );
        this.groupUtils = new GroupMessageUtil( this, this.getJsonXStream(), MediaType.APPLICATION_JSON );
    }

    private RepositoryResource createRepository()
    {
        RepositoryResource resource = new RepositoryResource();

        resource.setProvider( "maven2" );
        resource.setFormat( "maven2" );
        resource.setRepoPolicy( RepositoryPolicy.RELEASE.name() );
        resource.setChecksumPolicy( ChecksumPolicy.IGNORE.name() );
        resource.setBrowseable( true );
        resource.setIndexable( true );
        resource.setExposed( true );

        return resource;
    }

    protected void createHostedRepository()
        throws Exception
    {
        RepositoryResource resource = createRepository();
        resource.setId( HOSTED_REPO_ID );
        resource.setName( HOSTED_REPO_ID );
        resource.setRepoType( "hosted" );
        resource.setIndexable( true );
        resource.setWritePolicy( RepositoryWritePolicy.ALLOW_WRITE.name() );
        resource.setRepoPolicy( RepositoryPolicy.RELEASE.name() );
        resource.setNotFoundCacheTTL( 1440 );
        resource.setDownloadRemoteIndexes( false );

        repoUtils.createRepository( resource );

        TaskScheduleUtil.waitForAllTasksToStop();
    }

    protected void createProxyRepository()
        throws Exception
    {
        RepositoryResource resource = createRepository();
        resource.setId( PROXY_REPO_ID );
        resource.setName( PROXY_REPO_ID );
        resource.setRepoType( "proxy" );
        resource.setIndexable( true );
        resource.setWritePolicy( RepositoryWritePolicy.READ_ONLY.name() );
        resource.setDownloadRemoteIndexes( true );
        RepositoryResourceRemoteStorage remoteStorage = new RepositoryResourceRemoteStorage();
        remoteStorage.setRemoteStorageUrl( getBaseNexusUrl() + "content/repositories/" + HOSTED_REPO_ID + "/" );
        resource.setRemoteStorage( remoteStorage );
        resource.setRepoPolicy( RepositoryPolicy.RELEASE.name() );
        resource.setChecksumPolicy( ChecksumPolicy.IGNORE.name() );
        repoUtils.createRepository( resource );

        TaskScheduleUtil.waitForAllTasksToStop();
    }

    protected void createSecondHostedRepository()
        throws Exception
    {
        RepositoryResource resource = createRepository();
        resource.setId( SECOND_HOSTED_REPO_ID );
        resource.setName( SECOND_HOSTED_REPO_ID );
        resource.setRepoType( "hosted" );
        resource.setWritePolicy( RepositoryWritePolicy.ALLOW_WRITE.name() );
        resource.setRepoPolicy( RepositoryPolicy.RELEASE.name() );
        resource.setIndexable( true );
        repoUtils.createRepository( resource );

        TaskScheduleUtil.waitForAllTasksToStop();
    }

    protected void createThirdHostedRepository()
        throws Exception
    {
        RepositoryResource resource = createRepository();
        resource.setId( THIRD_HOSTED_REPO_ID );
        resource.setName( THIRD_HOSTED_REPO_ID );
        resource.setRepoType( "hosted" );
        resource.setWritePolicy( RepositoryWritePolicy.ALLOW_WRITE.name() );
        resource.setIndexable( true );
        repoUtils.createRepository( resource );

        TaskScheduleUtil.waitForAllTasksToStop();
    }

    protected void createGroup( String groupId, String... repoIds )
        throws Exception
    {
        RepositoryGroupResource group = new RepositoryGroupResource();
        group.setId( groupId );
        group.setName( groupId );
        group.setFormat( "maven2" );
        group.setExposed( true );
        group.setProvider( "maven2" );

        for ( String repoId : repoIds )
        {
            RepositoryGroupMemberRepository repo = new RepositoryGroupMemberRepository();
            repo.setId( repoId );
            group.addRepository( repo );
        }

        groupUtils.createGroup( group );

        TaskScheduleUtil.waitForAllTasksToStop();
    }

    protected String createReindexTask( String repositoryId, String taskName )
        throws Exception
    {
        ScheduledServicePropertyResource prop = new ScheduledServicePropertyResource();
        prop.setKey( "repositoryId" );

        if ( repositoryId.equals( GROUP_ID ) )
        {
            prop.setValue( repositoryId );
        }
        else
        {
            prop.setValue( repositoryId );
        }

        ScheduledServiceBaseResource scheduledTask = new ScheduledServiceBaseResource();
        scheduledTask.setEnabled( true );
        scheduledTask.setId( null );
        scheduledTask.setName( taskName );
        scheduledTask.setTypeId( UpdateIndexTaskDescriptor.ID );
        scheduledTask.setSchedule( "manual" );
        scheduledTask.addProperty( prop );
        Status status = TaskScheduleUtil.create( scheduledTask );

        Assert.assertTrue( status.isSuccess() );

        return TaskScheduleUtil.getTask( taskName ).getId();
    }

    protected String createHostedReindexTask()
        throws Exception
    {
        return createReindexTask( HOSTED_REPO_ID, HOSTED_REINDEX_TASK_NAME );
    }

    protected String createProxyReindexTask()
        throws Exception
    {
        return createReindexTask( PROXY_REPO_ID, PROXY_REINDEX_TASK_NAME );
    }

    protected String createSecondHostedReindexTask()
        throws Exception
    {
        return createReindexTask( SECOND_HOSTED_REPO_ID, SECOND_HOSTED_REINDEX_TASK_NAME );
    }

    protected void reindexRepository( String taskId, String taskName )
        throws Exception
    {
        TaskScheduleUtil.run( taskId );

        TaskScheduleUtil.waitForAllTasksToStop();
    }

    protected void reindexHostedRepository( String taskId )
        throws Exception
    {
        reindexRepository( taskId, HOSTED_REINDEX_TASK_NAME );
    }

    protected void reindexProxyRepository( String taskId )
        throws Exception
    {
        reindexRepository( taskId, PROXY_REINDEX_TASK_NAME );
    }

    protected void reindexSecondHostedRepository( String taskId )
        throws Exception
    {
        reindexRepository( taskId, SECOND_HOSTED_REINDEX_TASK_NAME );
    }

    protected File getRepositoryLocalIndexDirectory( String repositoryId )
    {
        return new File( AbstractNexusIntegrationTest.nexusWorkDir + "/indexer/" + repositoryId + "-local/" );
    }

    protected File getHostedRepositoryLocalIndexDirectory()
    {
        return getRepositoryLocalIndexDirectory( HOSTED_REPO_ID );
    }

    protected File getProxyRepositoryLocalIndexDirectory()
    {
        return getRepositoryLocalIndexDirectory( PROXY_REPO_ID );
    }

    protected File getSecondHostedRepositoryLocalIndexDirectory()
    {
        return getRepositoryLocalIndexDirectory( SECOND_HOSTED_REPO_ID );
    }

    protected File getThirdHostedRepositoryLocalIndexDirectory()
    {
        return getRepositoryLocalIndexDirectory( THIRD_HOSTED_REPO_ID );
    }

    protected File getGroupLocalIndexDirectory()
    {
        return getRepositoryLocalIndexDirectory( GROUP_ID );
    }

    protected File getRepositoryRemoteIndexDirectory( String repositoryId )
    {
        return new File( AbstractNexusIntegrationTest.nexusWorkDir + "/indexer/" + repositoryId + "-remote/" );
    }

    protected File getHostedRepositoryRemoteIndexDirectory()
    {
        return getRepositoryRemoteIndexDirectory( HOSTED_REPO_ID );
    }

    protected File getProxyRepositoryRemoteIndexDirectory()
    {
        return getRepositoryRemoteIndexDirectory( PROXY_REPO_ID );
    }

    protected File getSecondHostedRepositoryRemoteIndexDirectory()
    {
        return getRepositoryRemoteIndexDirectory( SECOND_HOSTED_REPO_ID );
    }

    protected File getThirdHostedRepositoryRemoteIndexDirectory()
    {
        return getRepositoryRemoteIndexDirectory( THIRD_HOSTED_REPO_ID );
    }

    protected File getGroupRemoteIndexDirectory()
    {
        return getRepositoryRemoteIndexDirectory( GROUP_ID );
    }

    protected File getRepositoryStorageDirectory( String repositoryId )
    {
        return new File( AbstractNexusIntegrationTest.nexusWorkDir + "/storage/" + repositoryId + "/" );
    }

    protected File getHostedRepositoryStorageDirectory()
    {
        return getRepositoryStorageDirectory( HOSTED_REPO_ID );
    }

    protected File getProxyRepositoryStorageDirectory()
    {
        return getRepositoryStorageDirectory( PROXY_REPO_ID );
    }

    protected File getSecondHostedRepositoryStorageDirectory()
    {
        return getRepositoryStorageDirectory( SECOND_HOSTED_REPO_ID );
    }

    protected File getThirdHostedRepositoryStorageDirectory()
    {
        return getRepositoryStorageDirectory( THIRD_HOSTED_REPO_ID );
    }

    protected File getGroupStorageDirectory()
    {
        return getRepositoryStorageDirectory( GROUP_ID );
    }

    protected File getRepositoryIndex( File directory )
    {
        return new File( directory, IndexingContext.INDEX_FILE_PREFIX + ".gz" );
    }

    protected File getHostedRepositoryIndex()
    {
        return getRepositoryIndex( getHostedRepositoryStorageIndexDirectory() );
    }

    protected File getProxyRepositoryIndex()
    {
        return getRepositoryIndex( getProxyRepositoryStorageIndexDirectory() );
    }

    protected File getSecondHostedRepositoryIndex()
    {
        return getRepositoryIndex( getSecondHostedRepositoryStorageIndexDirectory() );
    }

    protected File getThirdHostedRepositoryIndex()
    {
        return getRepositoryIndex( getThirdHostedRepositoryStorageIndexDirectory() );
    }

    protected File getGroupIndex()
    {
        return getRepositoryIndex( getGroupStorageIndexDirectory() );
    }

    protected Properties getRepositoryIndexProperties( File baseDir )
        throws Exception
    {
        Properties props = new Properties();

        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream( new File( baseDir, IndexingContext.INDEX_FILE_PREFIX + ".properties" ) );
            props.load( fis );
        }
        finally
        {
            if ( fis != null )
            {
                fis.close();
            }
        }

        return props;
    }

    protected Properties getHostedRepositoryIndexProperties()
        throws Exception
    {
        return getRepositoryIndexProperties( getHostedRepositoryStorageIndexDirectory() );
    }

    protected Properties getProxyRepositoryIndexProperties()
        throws Exception
    {
        return getRepositoryIndexProperties( getProxyRepositoryStorageIndexDirectory() );
    }

    protected Properties getSecondHostedRepositoryIndexProperties()
        throws Exception
    {
        return getRepositoryIndexProperties( getSecondHostedRepositoryStorageIndexDirectory() );
    }

    protected Properties getThirdHostedRepositoryIndexProperties()
        throws Exception
    {
        return getRepositoryIndexProperties( getThirdHostedRepositoryStorageIndexDirectory() );
    }

    protected Properties getGroupIndexProperties()
        throws Exception
    {
        return getRepositoryIndexProperties( getGroupStorageIndexDirectory() );
    }

    protected File getRepositoryIndexIncrement( File directory, String id )
    {
        return new File( directory, IndexingContext.INDEX_FILE_PREFIX + "." + id + ".gz" );
    }

    protected File getHostedRepositoryIndexIncrement( String id )
    {
        return getRepositoryIndexIncrement( getHostedRepositoryStorageIndexDirectory(), id );
    }

    protected File getProxyRepositoryIndexIncrement( String id )
    {
        return getRepositoryIndexIncrement( getProxyRepositoryStorageIndexDirectory(), id );
    }

    protected File getSecondHostedRepositoryIndexIncrement( String id )
    {
        return getRepositoryIndexIncrement( getSecondHostedRepositoryStorageIndexDirectory(), id );
    }

    protected File getThirdHostedRepositoryIndexIncrement( String id )
    {
        return getRepositoryIndexIncrement( getThirdHostedRepositoryStorageIndexDirectory(), id );
    }

    protected File getGroupIndexIncrement( String id )
    {
        return getRepositoryIndexIncrement( getGroupStorageIndexDirectory(), id );
    }

    protected File getRepositoryStorageIndexDirectory( String repositoryId )
    {
        return new File( AbstractNexusIntegrationTest.nexusWorkDir + "/storage/" + repositoryId + "/.index/" );
    }

    protected File getHostedRepositoryStorageIndexDirectory()
    {
        return getRepositoryStorageIndexDirectory( HOSTED_REPO_ID );
    }

    protected File getProxyRepositoryStorageIndexDirectory()
    {
        return getRepositoryStorageIndexDirectory( PROXY_REPO_ID );
    }

    protected File getSecondHostedRepositoryStorageIndexDirectory()
    {
        return getRepositoryStorageIndexDirectory( SECOND_HOSTED_REPO_ID );
    }

    protected File getThirdHostedRepositoryStorageIndexDirectory()
    {
        return getRepositoryStorageIndexDirectory( THIRD_HOSTED_REPO_ID );
    }

    protected File getGroupStorageIndexDirectory()
    {
        return getRepositoryStorageIndexDirectory( GROUP_ID );
    }

    protected void validateCurrentIncrementalCounter( Properties properties, Integer current )
        throws Exception
    {
        if ( current == null )
        {
            Assert.assertNull( properties.getProperty( IndexingContext.INDEX_CHUNK_COUNTER ) );
        }
        else
        {
            Assert.assertEquals( properties.getProperty( IndexingContext.INDEX_CHUNK_COUNTER ),
                Integer.toString( current ) );
        }
    }

    protected void validateCurrentHostedIncrementalCounter( Integer current )
        throws Exception
    {
        validateCurrentIncrementalCounter( getHostedRepositoryIndexProperties(), current );
    }

    protected void validateCurrentProxyIncrementalCounter( Integer current )
        throws Exception
    {
        validateCurrentIncrementalCounter( getProxyRepositoryIndexProperties(), current );
    }

    protected void validateCurrentSecondHostedIncrementalCounter( Integer current )
        throws Exception
    {
        validateCurrentIncrementalCounter( getSecondHostedRepositoryIndexProperties(), current );
    }

    protected void validateCurrentThirdHostedIncrementalCounter( Integer current )
        throws Exception
    {
        validateCurrentIncrementalCounter( getThirdHostedRepositoryIndexProperties(), current );
    }

    protected void validateCurrentGroupIncrementalCounter( Integer current )
        throws Exception
    {
        validateCurrentIncrementalCounter( getGroupIndexProperties(), current );
    }

    protected void searchForArtifactInIndex( String artifact, String repositoryId, boolean shouldFind )
        throws Exception
    {
        Map<String, String> args = new HashMap<String, String>();

        if ( FIRST_ARTIFACT.equals( artifact ) )
        {
            args.put( "a", "ant" );
        }
        else if ( SECOND_ARTIFACT.equals( artifact ) )
        {
            args.put( "a", "asm" );
        }
        else if ( THIRD_ARTIFACT.equals( artifact ) )
        {
            args.put( "a", "commons-attributes-api" );
        }
        else if ( FOURTH_ARTIFACT.equals( artifact ) )
        {
            args.put( "a", "commons-cli" );
        }
        else if ( FIFTH_ARTIFACT.equals( artifact ) )
        {
            args.put( "a", "commons-io" );
        }

        List<NexusArtifact> artifacts = getSearchMessageUtil().searchFor( args, repositoryId );

        Assert.assertEquals( artifacts.size() > 0, shouldFind );
    }

    protected void searchForArtifactInHostedIndex( String artifact, boolean shouldFind )
        throws Exception
    {
        searchForArtifactInIndex( artifact, HOSTED_REPO_ID, shouldFind );
    }

    protected void searchForArtifactInProxyIndex( String artifact, boolean shouldFind )
        throws Exception
    {
        searchForArtifactInIndex( artifact, PROXY_REPO_ID, shouldFind );
    }

    protected void searchForArtifactInSecondHostedIndex( String artifact, boolean shouldFind )
        throws Exception
    {
        searchForArtifactInIndex( artifact, SECOND_HOSTED_REPO_ID, shouldFind );
    }

    protected void deleteAllNonHiddenContent( File directory )
        throws Exception
    {
        if ( directory.isDirectory() && directory.exists() )
        {
            File[] files = directory.listFiles();

            if ( files != null && files.length > 0 )
            {
                for ( int i = 0; i < files.length; i++ )
                {
                    if ( files[i].isDirectory() )
                    {
                        if ( !files[i].getName().startsWith( "." ) )
                        {
                            FileUtils.deleteDirectory( files[i] );
                        }
                    }
                    else
                    {
                        files[i].delete();
                    }
                }
            }
        }
    }

    @BeforeClass( alwaysRun = true )
    public static void clean()
        throws Exception
    {
        cleanWorkDir();
    }

    protected void searchFor( String whereRepo, String... whatForArtifacts )
        throws Exception
    {
        for ( String artifact : whatForArtifacts )
        {
            searchForArtifactInIndex( artifact, whereRepo, true );
            searchForArtifactInIndex( artifact, GROUP_ID, true );
        }

        List<String> otherArtifacts = getArtifactBut( whatForArtifacts );

        for ( String artifact : otherArtifacts )
        {
            searchForArtifactInIndex( artifact, whereRepo, false );
        }

        List<String> repos = getReposBut( whereRepo );
        for ( String repoId : repos )
        {
            for ( String artifact : whatForArtifacts )
            {
                searchForArtifactInIndex( artifact, repoId, false );
            }
        }
    }

    protected List<String> getArtifactBut( String[] butArtifacts )
    {
        List<String> artifacts = new ArrayList<String>();
        artifacts.add( FIRST_ARTIFACT );
        artifacts.add( SECOND_ARTIFACT );
        artifacts.add( THIRD_ARTIFACT );
        artifacts.add( FOURTH_ARTIFACT );
        artifacts.add( FIFTH_ARTIFACT );
        artifacts.removeAll( Arrays.asList( butArtifacts ) );
        return artifacts;
    }

    protected List<String> getReposBut( String butRepo )
    {
        List<String> repos = new ArrayList<String>();
        repos.add( HOSTED_REPO_ID );
        repos.add( SECOND_HOSTED_REPO_ID );
        repos.add( THIRD_HOSTED_REPO_ID );
        repos.remove( butRepo );
        return repos;
    }
}
