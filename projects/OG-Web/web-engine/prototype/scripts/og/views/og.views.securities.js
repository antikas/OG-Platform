/*
 * @copyright 2011 - present by OpenGamma Inc
 * @license See distribution for license
 */
$.register_module({
    name: 'og.views.securities',
    dependencies: [
        'og.api.rest',
        'og.api.text',
        'og.common.masthead.menu',
        'og.common.routes',
        'og.common.search_results.core',
        'og.common.util.history',
        'og.common.util.ui.dialog',
        'og.common.util.ui.message',
        'og.common.util.ui.toolbar',
        'og.views.common.layout',
        'og.views.common.versions',
        'og.views.common.state',
        'og.views.common.default_details'
    ],
    obj: function () {
        var api = og.api,
            common = og.common,
            details = common.details,
            history = common.util.history,
            masthead = common.masthead,
            routes = common.routes,
            search, layout,
            ui = common.util.ui,
            module = this,
            page_name = module.name.split('.').pop(),
            check_state = og.views.common.state.check.partial('/' + page_name),
            view,
            toolbar_buttons = {
                'new': function () {ui.dialog({
                    type: 'input',
                    title: 'Add Securities',
                    fields: [
                        {type: 'select', name: 'Scheme Type', id: 'scheme-type',
                                options: [
                                    {name: 'Bloomberg Ticker', value: 'BLOOMBERG_TICKER'},
                                    {name: 'Bloomberg Ticker/Coupon/Maturity', value: 'BLOOMBERG_TCM'},
                                    {name: 'Bloomberg BUID', value: 'BLOOMBERG_BUID'},
                                    {name: 'CUSIP', value: 'CUSIP'},
                                    {name: 'ISIN', value: 'ISIN'},
                                    {name: 'RIC', value: 'RIC'},
                                    {name: 'SEDOL', value: 'CSEDOL1'}
                                ]
                        },
                        {type: 'textarea', name: 'Identifiers', id: 'identifiers'}
                    ],
                    buttons: {
                        'OK': function () {
                            $(this).dialog('close');
                            api.rest.securities.put({
                                handler: function (result) {
                                    var args = routes.current().args;
                                    if (result.error) return ui.dialog({type: 'error', message: result.message});
                                    view.search(args);
                                    if (result.data.data.length !== 1)
                                        return routes.go(routes.hash(module.rules.load, args));
                                    routes.go(routes.hash(module.rules.load_item, args, {
                                        add: {id: result.data.data[0].split('|')[1]},
                                        del: ['version']
                                    }));
                                },
                                scheme_type: ui.dialog({return_field_value: 'scheme-type'}),
                                identifier: ui.dialog({return_field_value: 'identifiers'})
                            });
                        }
                    }
                })},
                'delete': function () {ui.dialog({
                    type: 'confirm',
                    title: 'Delete Security?',
                    message: 'Are you sure you want to permanently delete this security?',
                    buttons: {
                        'Delete': function () {
                            $(this).dialog('close');
                            api.rest.securities.del({
                                id: routes.last().args.id,
                                handler: function (result) {
                                    var args = routes.current().args;
                                    if (result.error) return ui.dialog({type: 'error', message: result.message});
                                    view.search(args);
                                    routes.go(routes.hash(module.rules.load, args));
                                }
                            });
                        }
                    }
                })},
                'versions': function () {
                    var rule = module.rules.load_item, args = routes.current().args;
                    routes.go(routes.prefix() + routes.hash(rule, args, {add: {version: '*'}}));
                    if (!layout.inner.state.south.isClosed && args.version) {
                        layout.inner.close('south');
                    } else layout.inner.open('south');
                    layout.inner.options.south.onclose = function () {
                        routes.go(routes.hash(rule, args, {del: ['version']}));
                    };
                }
            },
            options = {
                slickgrid: {
                    'selector': '.OG-js-search', 'page_type': page_name,
                    'columns': [
                        {id: 'type', toolTip: 'type', name: null, field: 'type', width: 100},
                        {
                            id: 'name', toolTip: 'name', field: 'name', width: 300, cssClass: 'og-link',
                            name: '<input type="text" placeholder="Name" '
                                + 'class="og-js-name-filter" style="width: 280px;">'
                        }
                    ]
                },
                toolbar: {
                    'default': {
                        buttons: [
                            {id: 'new', tooltip: 'New', handler: toolbar_buttons['new']},
                            {id: 'save', tooltip: 'Save', enabled: 'OG-disabled'},
                            {id: 'saveas', tooltip: 'Save as', enabled: 'OG-disabled'},
                            {id: 'delete', tooltip: 'Delete', enabled: 'OG-disabled'}
                        ],
                        location: '.OG-tools'
                    },
                    active: {
                        buttons: [
                            {id: 'new', tooltip: 'New', handler: toolbar_buttons['new']},
                            {id: 'save', tooltip: 'Save', enabled: 'OG-disabled'},
                            {id: 'saveas', tooltip: 'Save as', enabled: 'OG-disabled'},
                            {id: 'delete', tooltip: 'Delete', divider: true, handler: toolbar_buttons['delete']},
                            {id: 'versions', label: 'versions', handler: toolbar_buttons['versions']}
                        ],
                        location: '.OG-tools'
                    }
                }
            },
            default_details = og.views.common.default_details.partial(page_name, 'Securities', options),
            details_page = function (args) {
                // load versions
                if (args.version) {
                    layout.inner.open('south');
                    og.views.common.versions.load();
                } else layout.inner.close('south');
                api.rest.securities.get({
                    dependencies: ['id'],
                    handler: function (result) {
                        if (result.error) return alert(result.message);
                        var json = result.data, text_handler,
                            security_type = json.template_data['securityType'].toLowerCase(),
                            template = module.name + '.' + security_type;
                        json.template_data.name = json.template_data.name || json.template_data.name.lang();
                        history.put({
                            name: json.template_data.name,
                            item: 'history.' + page_name + '.recent',
                            value: routes.current().hash
                        });
                        api.text({module: template, handler: text_handler = function (template, error) {
                            if (error) {
                                og.dev.warn('using default security template for security type: ' + security_type);
                                return api.text({module: module.name + '.default', handler: text_handler});
                            }
                            var error_html = '\
                                    <section class="OG-box og-box-glass og-box-error OG-shadow-light">\
                                        This security has been deleted\
                                    </section>\
                                ',
                                $html = $.tmpl(template, json.template_data), header, content,
                                html = [], id, json_id = json.identifiers;
                            header = $.outer($html.find('> header')[0]);
                            content = $.outer($html.find('> section')[0]);
                            $('.ui-layout-inner-center .ui-layout-header').html(header);
                            $('.ui-layout-inner-center .ui-layout-content').html(content);
                            if (!Object.keys(json_id)[0]) $('.ui-layout-inner-center .og-js-identifiers')
                                .html('<tr><td><span>' + ''.lang() + '</span></td><td></td></tr>');
                            else for (id in json_id) {
                                if (json_id.hasOwnProperty(id)) {
                                    html.push('<tr><td><span>', id.lang(),
                                              '<span></td><td>', json_id[id].replace(id + '-', ''), '</td></tr>');
                                }
                                $('.ui-layout-inner-center .og-js-identifiers').html(html.join(''));
                            }
                            (function () {
                                if (json.template_data['underlyingOid']) {
                                    var id = json.template_data['underlyingOid'],
                                        rule = module.rules.load_item,
                                        hash = routes.hash(rule, routes.current().args, {
                                            add: {id: id},
                                            del: ['version']
                                        }),
                                        text = json.template_data['underlyingName'] ||
                                            json.template_data['underlyingExternalId'],
                                        anchor = '<a class="og-js-live-anchor" href="' + routes.prefix() + hash + '">' +
                                            text + '</a>';
                                        $('.ui-layout-inner-center .OG-js-underlying-id').html(anchor);
                                }
                            }());
                            ui.toolbar(options.toolbar.active);
                            if (json.template_data && json.template_data.deleted) {
                                $('.ui-layout-inner-north').html(error_html);
                                layout.inner.sizePane('north', '0');
                                layout.inner.open('north');
                                $('.OG-tools .og-js-delete').addClass('OG-disabled').unbind();
                            } else {
                                layout.inner.close('north');
                                $('.ui-layout-inner-north').empty();
                            }
                            if (json.template_data.hts_id) common.gadgets.timeseries({
                                selector: '.OG-js-details-panel .og-js-timeseries',
                                id: json.template_data.hts_id
                            });
                            ui.message({location: '.ui-layout-inner-center', destroy: true});
                            setTimeout(layout.inner.resizeAll);
                        }});
                    },
                    id: args.id,
                    version: args.version && args.version !== '*' ? args.version : void 0,
                    loading: function () {
                        ui.message({
                            location: '.ui-layout-inner-center',
                            message: {0: 'loading...', 3000: 'still loading...'}
                        });
                    }
                });
            },
            state = {};
        module.rules = {
            load: {route: '/' + page_name + '/name:?/type:?', method: module.name + '.load'},
            load_filter: {route: '/' + page_name + '/filter:/:id?/name:?/type:?', method: module.name + '.load_filter'},
            load_item: {route: '/' + page_name + '/:id/name:?/version:?/type:?', method: module.name + '.load_item'}
        };
        return view = {
            filters: ['name', 'type'],
            load: function (args) {
                layout = og.views.common.layout;
                check_state({args: args, conditions: [
                    {new_page: function (args) {view.search(args), masthead.menu.set_tab(page_name);}}
                ]});
                if (!args.id) default_details();
            },
            load_filter: function (args) {
                var search_filter = function () {
                        var filter_name = options.slickgrid.columns[0].name;
                        if (!filter_name || filter_name === 'loading') // wait until type filter is populated
                            return setTimeout(search_filter, 500);
                        search.filter(args);
                };
                check_state({args: args, conditions: [{new_value: 'id', method: function (args) {
                    view[args.id ? 'load_item' : 'load'](args);
                }}]});
                search_filter();
            },
            load_item: function (args) {
                check_state({args: args, conditions: [
                    {new_page: function (args) {
                        view.load(args);
                        layout.inner.options.south.onclose = null;
                        layout.inner.close.partial('south');
                    }},
                    {new_value: 'id', method: function (args) {
                        layout.inner.options.south.onclose = null;
                        layout.inner.close.partial('south');
                    }}
                ]});
                view.details(args);
            },
            search: function (args) {
                if (!search) search = common.search_results.core();
                if (options.slickgrid.columns[0].name === 'loading')
                    return setTimeout(view.search.partial(args), 500);
                if (options.slickgrid.columns[0].name === null) return api.rest.securities.get({
                    meta: true,
                    handler: function (result) {
                        options.slickgrid.columns[0].name = [
                            '<select class="og-js-type-filter" style="width: 80px">',
                            result.data.types.reduce(function (acc, type) {
                                return acc + '<option value="' + type + '">' + type + '</option>';
                            }, '<option value="">Type</option>'),
                            '</select>'
                        ].join('');
                        view.search(args);
                    },
                    loading: function () {options.slickgrid.columns[0].name = 'loading';}
                });
                search.load($.extend(options.slickgrid, {url: args}));
            },
            details: details_page,
            init: function () {for (var rule in module.rules) routes.add(module.rules[rule]);},
            rules: module.rules
        };
    }
});