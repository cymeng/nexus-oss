/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2013 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugins.p2.repository.group;

import static org.sonatype.nexus.plugins.p2.repository.P2Constants.COMPOSITE_ARTIFACTS_XML;
import static org.sonatype.nexus.plugins.p2.repository.P2Constants.COMPOSITE_CONTENT_XML;
import static org.sonatype.nexus.plugins.p2.repository.P2Constants.P2_INDEX;
import static org.sonatype.nexus.plugins.p2.repository.internal.NexusUtils.createTemporaryP2Repository;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.sonatype.nexus.ApplicationStatusSource;
import org.sonatype.nexus.configuration.Configurator;
import org.sonatype.nexus.configuration.model.CRepository;
import org.sonatype.nexus.configuration.model.CRepositoryExternalConfigurationHolderFactory;
import org.sonatype.nexus.plugins.p2.repository.P2CompositeGroupRepository;
import org.sonatype.nexus.plugins.p2.repository.P2Constants;
import org.sonatype.nexus.plugins.p2.repository.P2ContentClass;
import org.sonatype.nexus.plugins.p2.repository.P2GroupRepository;
import org.sonatype.nexus.plugins.p2.repository.internal.NexusUtils;
import org.sonatype.nexus.proxy.IllegalOperationException;
import org.sonatype.nexus.proxy.ItemNotFoundException;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.StorageException;
import org.sonatype.nexus.proxy.access.Action;
import org.sonatype.nexus.proxy.events.NexusStartedEvent;
import org.sonatype.nexus.proxy.events.RepositoryEventLocalStatusChanged;
import org.sonatype.nexus.proxy.events.RepositoryGroupMembersChangedEvent;
import org.sonatype.nexus.proxy.events.RepositoryRegistryEventAdd;
import org.sonatype.nexus.proxy.item.DefaultStorageLinkItem;
import org.sonatype.nexus.proxy.item.RepositoryItemUid;
import org.sonatype.nexus.proxy.item.RepositoryItemUidLock;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.proxy.item.StorageLinkItem;
import org.sonatype.nexus.proxy.registry.ContentClass;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;
import org.sonatype.nexus.proxy.repository.AbstractGroupRepository;
import org.sonatype.nexus.proxy.repository.DefaultRepositoryKind;
import org.sonatype.nexus.proxy.repository.GroupRepository;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.repository.RepositoryKind;
import org.sonatype.p2.bridge.CompositeRepository;
import com.google.common.base.Throwables;
import com.google.common.eventbus.Subscribe;

/**
 * P2 group using P2 composite repositories.
 *
 * @since 2.6
 */
@Component( role = GroupRepository.class, hint = P2CompositeGroupRepositoryImpl.ROLE_HINT,
            instantiationStrategy = "per-lookup", description = "Eclipse P2 Composite" )
public class P2CompositeGroupRepositoryImpl
    extends AbstractGroupRepository
    implements P2CompositeGroupRepository, GroupRepository
{

    public static final String ROLE_HINT = "p2-composite";

    @Requirement( hint = P2ContentClass.ID )
    private ContentClass contentClass;

    @Requirement
    private P2GroupRepositoryConfigurator p2GroupRepositoryConfigurator;

    @Requirement
    private CompositeRepository compositeRepository;

    @Requirement
    private ApplicationStatusSource applicationStatusSource;

    @Requirement
    private RepositoryRegistry repositoryRegistry;

    private RepositoryKind repositoryKind;

    @Override
    protected Configurator getConfigurator()
    {
        return p2GroupRepositoryConfigurator;
    }

    @Override
    protected CRepositoryExternalConfigurationHolderFactory<?> getExternalConfigurationHolderFactory()
    {
        return new CRepositoryExternalConfigurationHolderFactory<P2GroupRepositoryConfiguration>()
        {
            @Override
            public P2GroupRepositoryConfiguration createExternalConfigurationHolder( final CRepository config )
            {
                return new P2GroupRepositoryConfiguration( (Xpp3Dom) config.getExternalConfiguration() );
            }
        };
    }

    @Override
    public RepositoryKind getRepositoryKind()
    {
        if ( repositoryKind == null )
        {
            repositoryKind =
                new DefaultRepositoryKind( GroupRepository.class,
                                           Arrays.asList( new Class<?>[]{ P2GroupRepository.class } ) );
        }
        return repositoryKind;
    }

    @Override
    public ContentClass getRepositoryContentClass()
    {
        return contentClass;
    }

    @Override
    protected StorageItem doRetrieveItem( final ResourceStoreRequest request )
        throws IllegalOperationException, ItemNotFoundException, StorageException
    {
        final RepositoryItemUid uid = createUid( P2Constants.METADATA_LOCK_PATH );
        final RepositoryItemUidLock lock = uid.getLock();
        final boolean requestGroupLocalOnly = request.isRequestGroupLocalOnly();
        try
        {
            lock.lock( Action.read );
            request.setRequestGroupLocalOnly( true );
            return super.doRetrieveItem( request );
        }
        finally
        {
            request.setRequestGroupLocalOnly( requestGroupLocalOnly );
            lock.unlock();
        }
    }

    @Override
    protected Collection<StorageItem> doListItems( final ResourceStoreRequest request )
        throws ItemNotFoundException, StorageException
    {
        final RepositoryItemUid uid = createUid( P2Constants.METADATA_LOCK_PATH );
        final RepositoryItemUidLock lock = uid.getLock();
        final boolean requestGroupLocalOnly = request.isRequestGroupLocalOnly();
        try
        {
            lock.lock( Action.read );
            request.setRequestGroupLocalOnly( true );
            return super.doListItems( request );
        }
        finally
        {
            request.setRequestGroupLocalOnly( requestGroupLocalOnly );
            lock.unlock();
        }
    }

    @Subscribe
    public void onEvent( final RepositoryGroupMembersChangedEvent event )
    {
        if ( this.equals( event.getRepository() ) )
        {
            prepareRepository( event.getNewRepositoryMemberIds() );
        }
    }

    @Subscribe
    public void onEvent( final RepositoryRegistryEventAdd event )
    {
        if ( this.equals( event.getRepository() ) )
        {
            prepareRepository( getMemberRepositoryIds() );
        }
    }

    @Subscribe
    public void onEvent( final NexusStartedEvent event )
    {
        prepareRepository( getMemberRepositoryIds() );
    }

    @Subscribe
    public void onEvent( final RepositoryEventLocalStatusChanged event )
    {
        if ( this.equals( event.getRepository() ) && event.getNewLocalStatus().shouldServiceRequest() )
        {
            prepareRepository( getMemberRepositoryIds() );
        }
    }

    private void prepareRepository( final List<String> memberRepositoryIds )
    {
        if ( !getLocalStatus().shouldServiceRequest()
            || !applicationStatusSource.getSystemStatus().isNexusStarted() )
        {
            return;
        }
        final RepositoryItemUid uid = createUid( P2Constants.METADATA_LOCK_PATH );
        final RepositoryItemUidLock lock = uid.getLock();
        try
        {
            lock.lock( Action.create );
            createMemberRepositoriesLinks( memberRepositoryIds );
            createCompositeXmls( memberRepositoryIds );
        }
        finally
        {
            lock.unlock();
        }
    }

    private void createCompositeXmls( final List<String> memberRepositoryIds )
    {
        try
        {
            File tempP2Repository = null;
            try
            {
                tempP2Repository = createTemporaryP2Repository();

                final URI[] memberRepositoryUris = toUris( memberRepositoryIds );

                compositeRepository.addArtifactsRepository( tempP2Repository.toURI(), memberRepositoryUris );
                compositeRepository.addMetadataRepository( tempP2Repository.toURI(), memberRepositoryUris );

                final RepositoryItemUid uid = createUid( P2Constants.METADATA_LOCK_PATH );
                final RepositoryItemUidLock lock = uid.getLock();
                try
                {
                    lock.lock( Action.create );

                    NexusUtils.storeItemFromFile(
                        COMPOSITE_ARTIFACTS_XML,
                        new File( tempP2Repository, COMPOSITE_ARTIFACTS_XML ),
                        this,
                        getMimeSupport().guessMimeTypeFromPath( COMPOSITE_ARTIFACTS_XML )
                    );
                    NexusUtils.storeItemFromFile(
                        COMPOSITE_CONTENT_XML,
                        new File( tempP2Repository, COMPOSITE_CONTENT_XML ),
                        this,
                        getMimeSupport().guessMimeTypeFromPath( COMPOSITE_CONTENT_XML )
                    );
                    NexusUtils.storeItem(
                        this,
                        new ResourceStoreRequest( P2_INDEX ),
                        getClass().getResourceAsStream( "/META-INF/p2Composite.index" ),
                        getMimeSupport().guessMimeTypeFromPath( P2_INDEX ),
                        null
                    );
                }
                finally
                {
                    lock.unlock();
                }
            }
            finally
            {
                FileUtils.deleteDirectory( tempP2Repository );
            }
        }
        catch ( Exception e )
        {
            throw Throwables.propagate( e );
        }
    }

    private void createMemberRepositoriesLinks( final List<String> memberRepositoryIds )
    {
        try
        {
            final Collection<StorageItem> rootItems = list( false, new ResourceStoreRequest( "/", true, false ) );
            for ( StorageItem rootItem : rootItems )
            {
                if ( rootItem instanceof StorageLinkItem )
                {
                    final RepositoryItemUid target = ( (StorageLinkItem) rootItem ).getTarget();
                    if ( "/".equals( target.getPath() ) )
                    {
                        deleteItem( false, new ResourceStoreRequest( rootItem.getPath() ) );
                    }
                }
            }
            for ( String memberRepositoryId : memberRepositoryIds )
            {
                final Repository repository = repositoryRegistry.getRepository( memberRepositoryId );
                final DefaultStorageLinkItem linkItem = new DefaultStorageLinkItem(
                    this,
                    new ResourceStoreRequest( "/" + memberRepositoryId ),
                    true,
                    true,
                    repository.createUid( "/" )
                );
                storeItem( false, linkItem );
            }
        }
        catch ( Exception e )
        {
            throw Throwables.propagate( e );
        }
    }

    private URI[] toUris( final List<String> memberRepositoryIds )
    {
        if ( memberRepositoryIds == null || memberRepositoryIds.isEmpty() )
        {
            return new URI[0];
        }
        final URI[] uris = new URI[memberRepositoryIds.size()];
        for ( int i = 0; i < memberRepositoryIds.size(); i++ )
        {
            try
            {
                uris[i] = new URI( memberRepositoryIds.get( i ) );
            }
            catch ( URISyntaxException e )
            {
                throw Throwables.propagate( e );
            }
        }
        return uris;
    }

}
