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
/*
 =================== DO NOT EDIT THIS FILE ====================
 Generated by Modello 1.3 on 2010-10-25 11:16:11,
 any modifications will be overwritten.
 ==============================================================
 */

package org.sonatype.nexus.configuration.model.v1_4_4.upgrade;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/


/**
 * Converts from version 1.4.4 (with version in package name) to
 * version 1.4.4 (without version in package name) of the model.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings( "all" )
public interface VersionConverter
{

    //-----------/
    // - Methods -/
    // -----------/

  /**
     * @param cErrorReporting
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CErrorReporting convertCErrorReporting( org.sonatype.nexus.configuration.model.v1_4_3.CErrorReporting cErrorReporting );

    /**
     * @param cHttpProxySettings
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CHttpProxySettings convertCHttpProxySettings( org.sonatype.nexus.configuration.model.v1_4_3.CHttpProxySettings cHttpProxySettings );

    /**
     * @param cLocalStorage
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CLocalStorage convertCLocalStorage( org.sonatype.nexus.configuration.model.v1_4_3.CLocalStorage cLocalStorage );

    /**
     * @param cMirror
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CMirror convertCMirror( org.sonatype.nexus.configuration.model.v1_4_3.CMirror cMirror );

    /**
     * @param cNotification
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CNotification convertCNotification( org.sonatype.nexus.configuration.model.v1_4_3.CNotification cNotification );

    /**
     * @param cNotificationTarget
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CNotificationTarget convertCNotificationTarget( org.sonatype.nexus.configuration.model.v1_4_3.CNotificationTarget cNotificationTarget );

    /**
     * @param cPathMappingItem
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CPathMappingItem convertCPathMappingItem( org.sonatype.nexus.configuration.model.v1_4_3.CPathMappingItem cPathMappingItem );

    /**
     * @param cPlugin
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CPlugin convertCPlugin( org.sonatype.nexus.configuration.model.v1_4_3.CPlugin cPlugin );

    /**
     * @param cProps
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CProps convertCProps( org.sonatype.nexus.configuration.model.v1_4_3.CProps cProps );

    /**
     * @param cRemoteAuthentication
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CRemoteAuthentication convertCRemoteAuthentication( org.sonatype.nexus.configuration.model.v1_4_3.CRemoteAuthentication cRemoteAuthentication );

    /**
     * @param cRemoteConnectionSettings
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CRemoteConnectionSettings convertCRemoteConnectionSettings( org.sonatype.nexus.configuration.model.v1_4_3.CRemoteConnectionSettings cRemoteConnectionSettings );

    /**
     * @param cRemoteHttpProxySettings
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CRemoteHttpProxySettings convertCRemoteHttpProxySettings( org.sonatype.nexus.configuration.model.v1_4_3.CRemoteHttpProxySettings cRemoteHttpProxySettings );

    /**
     * @param cRemoteNexusInstance
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CRemoteNexusInstance convertCRemoteNexusInstance( org.sonatype.nexus.configuration.model.v1_4_3.CRemoteNexusInstance cRemoteNexusInstance );

    /**
     * @param cRemoteStorage
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CRemoteStorage convertCRemoteStorage( org.sonatype.nexus.configuration.model.v1_4_3.CRemoteStorage cRemoteStorage );

    /**
     * @param cRepository
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CRepository convertCRepository( org.sonatype.nexus.configuration.model.v1_4_3.CRepository cRepository );

    /**
     * @param cRepositoryGrouping
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CRepositoryGrouping convertCRepositoryGrouping( org.sonatype.nexus.configuration.model.v1_4_3.CRepositoryGrouping cRepositoryGrouping );

    /**
     * @param cRepositoryTarget
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CRepositoryTarget convertCRepositoryTarget( org.sonatype.nexus.configuration.model.v1_4_3.CRepositoryTarget cRepositoryTarget );

    /**
     * @param cRestApiSettings
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CRestApiSettings convertCRestApiSettings( org.sonatype.nexus.configuration.model.v1_4_3.CRestApiSettings cRestApiSettings );

    /**
     * @param cRouting
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CRouting convertCRouting( org.sonatype.nexus.configuration.model.v1_4_3.CRouting cRouting );

    /**
     * @param cScheduleConfig
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CScheduleConfig convertCScheduleConfig( org.sonatype.nexus.configuration.model.v1_4_3.CScheduleConfig cScheduleConfig );

    /**
     * @param cScheduledTask
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CScheduledTask convertCScheduledTask( org.sonatype.nexus.configuration.model.v1_4_3.CScheduledTask cScheduledTask );

    /**
     * @param cSmtpConfiguration
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.CSmtpConfiguration convertCSmtpConfiguration( org.sonatype.nexus.configuration.model.v1_4_3.CSmtpConfiguration cSmtpConfiguration );

    /**
     * @param configuration
     */
    public org.sonatype.nexus.configuration.model.v1_4_4.Configuration convertConfiguration( org.sonatype.nexus.configuration.model.v1_4_3.Configuration configuration );
}
