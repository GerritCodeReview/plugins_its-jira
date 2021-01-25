// Copyright (C) 2021 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the 'License');
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an 'AS IS' BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

(function () {
  'use strict';

  Polymer({
    is: 'cs-its-jira-config',

    properties: {
      repoName: String,
      readOnly: {
        type: Boolean,
        value: true,
      },
      _config: Object,
      _changedConfig: Object,
      _prefsChanged: {
        type: Boolean,
        value: false,
      },
      _projectRestApi: Object,
    },

    attached() {
      this._projectRestApi = this.plugin.restApi('/projects/');
      this._getPreferences().then(() => {
        this._changedConfig = Object.assign({}, this._config);
      })
    },

    _getPreferences() {
      return this._projectRestApi.get(`${encodeURIComponent(this.repoName)}/config`)
        .then(config => {
          if (!config) {
            return;
          }
          if (config.plugin_config && config.plugin_config["its-jira"]) {
            this._config = config.plugin_config["its-jira"];
          }
        })
    },

    _handleListDataChanged(event) {
      this._changedConfig[event.target.id] = { values: event.detail.allEntries };
      this._handlePrefsChanged();
    },

    _handlePrefsChanged() {
      this._prefsChanged = true;
    },

    _handlePrefsSave() {
      let body = { plugin_config_values: {} };
      body.plugin_config_values['its-jira'] = this._changedConfig;
      this._projectRestApi.put(`${encodeURIComponent(this.repoName)}/config`, body)
        .then(() => {
          this._prefsChanged = false;
        }).catch(response => {
          this.fire('show-error', { message: response });
        });
    },
  });
})();
