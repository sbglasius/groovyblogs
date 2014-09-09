;
(function ($, window, document, undefined) {

    var pluginName = "fetchThumbnail",
        defaults = {};

    // The actual plugin constructor
    function Plugin(element, options) {
        this.element = element;
        this.$element = $(element);
        this.settings = $.extend({}, defaults, options);
        this._defaults = defaults;
        this._name = pluginName;
        this.url = this.$element.data('thumbnail');
        this.count = 0;
        this.init();
    }

    $.extend(Plugin.prototype, {
        init: function () {

            var self = this;
            // Give the page a chance to load then fetch the images
            setTimeout(function () {
                self.tryFetch.call(self)
            }, 200);
            console.log("xD");
        },
        tryFetch: function () {
            var self = this;
            $.get(self.url).done(function (data) {
                // if the image is loaded, then add an img tag and use the url (it should be cashed by the browser)
                var img = $('<img/>').attr('src', self.url).addClass('thumbnail','img-img-thumbnail');
                self.$element.replaceWith(img);
            }).fail(function (error) {
                // if the image is not loaded, retry in a little bit.
                if(self.count++ > 2) {
                    console.debug(self.count, $('i',self.$element));
                   $('i',self.$element).removeClass('fa-circle-o-notch fa-spin').addClass('fa-picture-o')
                }
                setTimeout(function () {
                    self.tryFetch.call(self);
                },1000*(self.count))
            })
        }
    });

    $.fn[ pluginName ] = function (options) {
        this.each(function () {
            // Do not add this to elements without a data-thumbnail
            if (!$.data(this, "plugin_" + pluginName) && $(this).data('thumbnail')) {
                $.data(this, "plugin_" + pluginName, new Plugin(this, options));
            }
        });

        // chain jQuery functions
        return this;
    };

})(jQuery, window, document);