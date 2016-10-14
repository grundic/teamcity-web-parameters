'use strict';

var WebParametersControl = {
    init: function (element_id) {
        $j("#" + element_id).select2({
            templateResult: this.addOptionImage,
            templateSelection: this.addOptionImage
        });
    },

    addOptionImage: function (opt) {
        if (!opt.id) {
            return opt.text;
        }
        var optimage = $j(opt.element).data('image');
        if (!optimage) {
            return opt.text;
        } else {
            return $j(
                '<span><img src="' + optimage + '" style="height:16px;" /> ' + $j(opt.element).text() + '</span>'
            );
        }
    }
};