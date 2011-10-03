/*
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
Sonatype.repoServer.PluginInfoTab = function(config) {
  var config = config || {};
  var defaultConfig = {
    labelClass : 'font: bold 12px tahoma, arial, helvetica, sans-serif;',
    textClass : 'font: normal 12px tahoma, arial, helvetica, sans-serif; padding: 0px 0px 0px 15px;'
  };
  Ext.apply(this, config, defaultConfig);

  Sonatype.repoServer.PluginInfoTab.superclass.constructor.call(this, {
        frame : true,
        items : [{
              xtype : 'panel',
              style : 'padding: 10px 0px 0px 10px;',
              layout : 'table',
              layoutConfig : {
                columns : 2
              },
              items : [{
                    xtype : 'label',
                    html : 'Name',
                    style : this.labelClass,
                    width : 120
                  }, {
                    xtype : 'label',
                    name : 'name',
                    style : this.textClass,
                    width : 320
                  }, {
                    xtype : 'label',
                    html : 'Version',
                    style : this.labelClass,
                    width : 120
                  }, {
                    xtype : 'label',
                    name : 'version',
                    style : this.textClass,
                    width : 320
                  }, {
                    xtype : 'label',
                    html : 'Status',
                    style : this.labelClass,
                    width : 120
                  }, {
                    xtype : 'label',
                    name : 'status',
                    style : this.textClass,
                    width : 320
                  }, {
                    xtype : 'label',
                    html : 'Description',
                    style : this.labelClass,
                    width : 120
                  }, {
                    xtype : 'label',
                    name : 'description',
                    style : this.textClass,
                    width : 320
                  }, {
                    xtype : 'label',
                    html : 'SCM Version',
                    style : this.labelClass,
                    width : 120
                  }, {
                    xtype : 'label',
                    name : 'scmVersion',
                    style : this.textClass,
                    width : 320
                  }, {
                    xtype : 'label',
                    html : 'SCM Timestamp',
                    style : this.labelClass,
                    width : 120
                  }, {
                    xtype : 'label',
                    name : 'scmTimestamp',
                    style : this.textClass,
                    width : 320
                  }]
            }],
        listeners : {
          beforerender : {
            fn : this.beforerenderHandler,
            scope : this
          }
        }
      });
};

Ext.extend(Sonatype.repoServer.PluginInfoTab, Ext.Panel, {
      beforerenderHandler : function(panel) {
        this.find('name', 'name')[0].setText(this.payload.data.name);
        this.find('name', 'version')[0].setText(this.payload.data.version);
        this.find('name', 'description')[0].setText(this.payload.data.description);
        this.find('name', 'status')[0].setText(this.capitalizeHead(this.payload.data.status));
        this.find('name', 'scmVersion')[0].setText(this.payload.data.scmVersion);
        this.find('name', 'scmTimestamp')[0].setText(this.payload.data.scmTimestamp);

        var pluginPropertiesPanel = this.items.get(0);

        var site = this.payload.data.site;
        if (!Ext.isEmpty(site))
        {
          pluginPropertiesPanel.add({
                xtype : 'label',
                html : 'Site',
                style : this.labelClass,
                width : 120
              });
          pluginPropertiesPanel.add({
                xtype : 'label',
                name : 'site',
                html : '<a href="' + site + '" target="_blank">' + site + '</a>',
                style : this.textClass
              });
        }

        var documentation = this.payload.data.documentation;
        if (!Ext.isEmpty(documentation) && documentation.length != 0)
        {
          pluginPropertiesPanel.add({
                xtype : 'label',
                html : 'Documentation',
                style : this.labelClass,
                width : 120
              });

          var link = '';
          for (var i = 0; i < documentation.length; i++)
          {
            if (i != 0)
            {
              link += ', ';
            }
            link += '<a href="' + documentation[i].url + '" target="_blank">' + documentation[i].label + '</a>';
          }
          pluginPropertiesPanel.add({
                xtype : 'label',
                name : 'site',
                html : link,
                style : this.textClass
              });
        }

        var failureReason = this.payload.data.failureReason;
        if (failureReason)
        {
          var html = '<h4 style="color:red;">This plugin was not able to be activated</h4><br/>';
          html = html + '<pre> ' + failureReason + '</pre><br/>';
          this.add({
                xtype : 'panel',
                frame : true,
                style : 'padding: 20px 0px 0px 10px;',
                autoScroll : true,
                html : html
              });
        }
      },

      capitalizeHead : function(str) {
        if (Ext.isEmpty(str))
        {
          return str;
        }
        return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
      }
    });

Sonatype.Events.addListener('pluginInfoInit', function(cardPanel, rec, gridPanel) {
      cardPanel.add(new Sonatype.repoServer.PluginInfoTab({
            name : 'info',
            tabTitle : 'Information',
            payload : rec
          }));
    });

Sonatype.repoServer.RestInfoTab = function(config) {
  var config = config || {};
  var defaultConfig = {};
  Ext.apply(this, config, defaultConfig);

  this.restInfoStore = new Ext.data.SimpleStore({
        id : 0,
        fields : [{
              name : 'URI',
              sortType : Ext.data.SortTypes.asUCString
            }, {
              name : 'description'
            }]
      });

  Sonatype.repoServer.PluginInfoTab.superclass.constructor.call(this, {
        style : 'padding: 5px 0px 0px 5px;',
        store : this.restInfoStore,
        columns : [{
              id : 'URI',
              header : 'URI',
              dataIndex : 'URI',
              width : 400,
              renderer : function(value) {
                return '<a href="' + Sonatype.config.servicePath + value + '" target="_blank">' + value + "</a>";
              }
            }
        // currently the server side is not able to return this field, I simply
        // keep the cod here.
        // { header: 'Description', dataIndex: 'description', width: 400 }
        ],
        autoExpandColumn : 'URI',
        listeners : {
          beforerender : {
            fn : this.beforerenderHandler,
            scope : this
          }
        }
      });
}

Ext.extend(Sonatype.repoServer.RestInfoTab, Ext.grid.GridPanel, {
      beforerenderHandler : function(panel) {
        var restInfos = this.payload.data.restInfos;
        if (restInfos && restInfos.length > 0)
        {
          this.restInfoStore.removeAll();
          for (var i = 0; i < restInfos.length; i++)
          {
            var restInfoRec = new Ext.data.Record(restInfos[i], restInfos[i].URI);
            this.restInfoStore.addSorted(restInfoRec);
          }
        }
      }
    });
