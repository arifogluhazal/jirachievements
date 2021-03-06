(function($) {
  
  $(document).ready(function() {

    $.getJSON(AJS.params.baseURL + '/rest/jirachievement/1.0/achievementlevels/count', function(data) {
      if (data === null)
      {
        return;
      }

      var userHeader = $('#header-details-user');
      var content =
        '<div id="header-user-achievements">' +
          '<span title="' + data.RED + ' red achievements">' +
            '<span class="achievement-red">&nbsp;</span>' +
            '<span id="achievement-red-count" class="achievement-count">' + data.RED + '</span>' +
          '</span>' +
          '<span title="' + data.BRONZE + ' bronze achievements">' +
            '<span class="achievement-bronze">&nbsp;</span>' +
            '<span id="achievement-bronze-count" class="achievement-count">' + data.BRONZE + '</span>' +
          '</span>' +
          '<span title="' + data.SILVER + ' silver achievements">' +
            '<span class="achievement-silver">&nbsp;</span>' +
            '<span  id="achievement-silver-count" class="achievement-count">' + data.SILVER + '</span>' +
          '</span>' +
          '<span title="' + data.GOLD + ' gold achievements">' +
            '<span class="achievement-gold">&nbsp;</span>' +
            '<span  id="achievement-gold-count" class="achievement-count">' + data.GOLD + '</span>' +
          '</span>' +
        '</div>';

      userHeader.prepend(content);
    });
  });

})(jQuery);