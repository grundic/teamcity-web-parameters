'use strict';

var WebParametersControl = {
    init: function (element_id) {
        $j("#" + element_id).selectize({
            duplicates: true,
            render: {
                option: this.doRender,
                item: this.doRender
            }
        });
    },

    doRender: function (item, escape) {
        var result = '<div>';
        if (item.image) {
            result += '<img src="' + escape(item.image) + '" style="height:16px; vertical-align:middle">';
        }
        return result + "<span>" + escape(item.text) + '</span></div>';
    }
};