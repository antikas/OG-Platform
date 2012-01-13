    /**
 * @copyright 2009 - present by OpenGamma Inc
 * @license See distribution for license
 */
$.register_module({
    name: 'og.views.common.versions',
    dependencies: ['og.common.util.ui.message', 'og.views.common.layout'],
    obj: function () {
        var PANEL = '.ui-layout-inner-south',
            HEADER = PANEL + ' .ui-layout-header',
            CONTENT = PANEL + ' .ui-layout-content',
            FOOTER = PANEL + ' .ui-layout-footer',
            versions;
        return versions = {
            load: function () {
                versions.setup();
                var ui = og.common.util.ui, routes = og.common.routes, cur = routes.current();
                if (!routes.current().args.id) {versions.clear()}
                og.api.rest[cur.page.substring(1)].get({
                    id: cur.args.id, version: '*',
                    handler: function (r) {
                        var cols = '<colgroup></colgroup><colgroup></colgroup><colgroup></colgroup>',
                        build_url = function (version) {
                            var args = routes.current().args, page = routes.current().page.substring(1);
                            return routes.hash(og.views[page].rules.load_item, args, {add: {version: version}});
                        },
                        $list = $(r.data.data.reduce(function (acc, val, i) {
                            var arr = val.split('|'), cur, sel, ver = routes.current().args.version;
                            //version_id
                            cur = !i ? '<span> Latest</span>' : '';
                            sel = ver === arr[0] ? ' class="og-selected"' : '';
                            return acc +
                                '<tr' + sel + '>' +
                                    '<td><a href="#' + build_url(arr[0]) + '">' + arr[0] + '</a>' + cur + '</td>' +
                                    '<td>' + arr[1] + '</td>' +
                                    '<td>' + og.common.util.date(arr[2]) + '</td>' +
                                '</tr>';
                        }, '<div class="og-container"><table>' + cols) + '</table></div>')
                        .click(function (e) {
                            var version = $(e.target).parents('tbody tr').find('td:first-child a').text();
                            if (version) routes.go(build_url(version));
                        });
                        $(CONTENT).html($list);
                        ui.message({location: '.ui-layout-inner-south', destroy: true});
                        og.views.common.layout.main.resizeAll();
                    },
                    loading: function () {
                        ui.message({
                            location: '.ui-layout-inner-south',
                            css: {bottom: '1px'},
                            message: {0: 'loading...', 3000: 'still loading...'}
                        });
                    }
                });

            },
            clear: function () {$(CONTENT).empty()},
            setup: function () {
                var layout = og.views.common.layout, routes = og.common.routes,
                    header_html = '\
                        <div><header><h2>Version History</h2></header></div>\
                        <div class="og-version-header">\
                          <table>\
                            <colgroup></colgroup><colgroup></colgroup><colgroup></colgroup>\
                            <thead><tr><th>Reference</th><th>Name</th><th>Valid from</th></tr></thead>\
                          </table>\
                        </div>\
                        <div class="og-divider"></div>'
                    ;
                if (!$(HEADER).length || (routes.last() && !routes.last().args.version)) $(PANEL).html( '\
                    <div class="ui-layout-header">' + header_html + '</div>\
                    <div class="ui-layout-content"></div>'
                ).removeClass(function (i , classes) {
                    var matches = classes.match(/OG-(?:.+)/g) || [];
                    return matches.join(' ');
                }).addClass('OG-versions');
                og.views.common.layout.inner.initContent('south');
            }
        }
    }
});