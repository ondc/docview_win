/**
 * Copyright 2013 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var totalSize = 1;
var id = $.url().segment(2);
var uuid = id;
var params = $.url().param();
$(document).ready(function() {
	
	$.ajax({
		type: "GET",
		url: '/view/' + uuid + '.json?start=1&size=0',
		data: params,
		async: false,
		dataType: "json"
	}).done(function( data ) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			uuid = data.uuid;
			var pages = data.data;
			totalSize = data.totalSize;
			if (totalSize < 3) {
				$('.bottom-paging-progress').hide();
				$('.paging-bottom-all').hide();
			}
			
			// title
			$('.navbar-inner .container-fluid .btn-navbar').after('<a class="brand lnk-file-title" style="text-decoration: none;" href="/doc/download/' + uuid + '" title="' + data.name + '">' + data.name + '</a>');
	
			// clear progress bar
			clearProgress();
			
			// pages
			// $('.span12').append('<div class="word-page"><div class="word-content"></div></div>');
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				$('.span12 .word-page .word-content').append(page.content);
				// $('.span12').append('<div class="word-page"><div class="word-content">' + page.content + '</div></div>');
			}
			
			bindBottomPagingProgress();
			
			// bottom paging positioning
			var onePagePercent = 100 / totalSize;
			var all = totalSize * onePagePercent;
			for (var i = 0; i < totalSize; i++) {
				$('.paging-bottom-all').append('<div class="paging-bottom-sub" page-num="' + (i + 1) + '" style="width: ' + onePagePercent + '%;">·</div>');
			}
			$(".paging-bottom-sub").click(function(){
				var id = $(this).attr('page-num');
				gotoAnchor(id);
			});
			
			// NEXT page link
			$('.span12').parent().append('<a id="next" href="/view/' + uuid + '.json?start=2&size=5"></a>');
			
			afterLoad();
		} else {
			$('.span12').html('<div class="alert alert-error">' + data.desc + '</div>');
		}

		// clear progress bar
		clearProgress();
	});
});