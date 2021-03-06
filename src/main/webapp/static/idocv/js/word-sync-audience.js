﻿/**
 * Copyright 2015 I Doc View
 * 
 * @author Godwin <godwin668@gmail.com>
 */

// predefined params in word-pdf-single.js
// slideUrls, curSlide, uuid, sessionId

// draw server
var drawServer = $('.server-param-container :text[key="conf-draw-server"]')
		.val();

var canvasArray = new Array();

var doc = $(document);
var win = $(window);
var canvas;
var ctx;
var img;

// all screen lines
var lines = [];

// Generate an unique ID
var id = Math.round($.now() * Math.random());

// A flag for drawing activity
var drawing = false;

var clients = {};
var cursors = {};

// previous position
var prev = {};
// current position
var curr = {};
// percent position
var perc = {};
// points (from -> to)
var p;

var socket = io.connect(drawServer);

$(document).ready(function() {
	
	// Check whether current browser support canvas
	if (!('getContext' in document.createElement('canvas'))) {
		alert('对不起，您的浏览器不支持画笔同步，推荐您使用新版Chrome或360浏览器！');
		return false;
	}
	
	gotoSlideSync(1);

	// receive socket event
	// receive moving event
	socket.on('moving', function (data) {
		var remoteUuid = data.uuid;
		if (uuid != remoteUuid) {
			return;
		}
		
		if(! (data.id in clients)){
			// a new user has come online. create a cursor for them
			cursors[data.id] = $('<div class="cursor">').appendTo('#cursors');
		}
		
		var absX = data.x * canvas.width;
		var absY = data.y * canvas.height;

		// Move the mouse pointer
		cursors[data.id].css({
			'left' : absX,
			'top' : absY
		});
		
		// Is the user drawing?
		if(data.drawing && clients[data.id]){
			
			// Draw a line on the canvas. clients[data.id] holds
			// the previous position of this user's mouse pointer
			
			var preAbsX = clients[data.id].x * canvas.width;
			var preAbsY = clients[data.id].y * canvas.height;
			drawLine(preAbsX, preAbsY, absX, absY);
			
			// save percent points
			p = {x1:clients[data.id].x, y1:clients[data.id].y, x2:data.x, y2:data.y};
			lines.push(p);
		}
		
		// Saving the current client state
		clients[data.id] = data;
		clients[data.id].updated = $.now();
	});

	// receive flip event
	socket.on('flip', function (data) {
		var remoteUuid = data.uuid;
		if (uuid != remoteUuid) {
			return;
		}
		gotoSlideSync(data.page);
	});

	// send flip event
	// Send page turning command
	$('.btn-cmd').on('click touchstart', function(e) {
		e.preventDefault();
		if ($(this).attr('cmd-string')) {
			var cmdString = $(this).attr('cmd-string');
			var pageNum = 0;
			if ('left' == cmdString) {
				preSlideSync();
			} else if ('right' == cmdString) {
				nextSlideSync();
			}/* else if ('clear' == cmdString) {
				socket.emit('clear', {
					'uuid' : uuid,
				});
				ctx.clearRect(0, 0, canvas.width, canvas.height);
				// location.reload();
			}*/
			// iosocket.send($(this).attr('data-key'));
		}
	});
	
	// page selector
	$('.select-page-selector-sync').val(curSlide);
	$('.select-page-selector-sync').change(function() {
		var selectNum = $(".select-page-selector-sync option:selected").text();
		gotoSlideSync(selectNum);
	});
	
	// send draw event
	
	// keyboard
	$(document).keydown(function(event){
		if (event.keyCode == 37 || event.keyCode == 38) {
			preSlideSync();
		} else if (event.keyCode == 39 || event.keyCode == 40 || event.keyCode == 32){
			nextSlideSync();
		} else if (event.keyCode == 13) {
			var isFullscreen = $(document).fullScreen() ? true : false;
			$('.img-container-sync').toggleFullScreen();
		}
	});
});

// bind canvas event: start -> move -> end
function bindCanvasEvent() {
	// start
	$('canvas').bind(
			'mousedown touchstart',
			function(e) {
				var type = e.type;
				var oleft = img.offset().left;
				var otop = img.offset().top;
				if ("mousedown" == type) {
					curr.x = Math.round(e.pageX - oleft);
					curr.y = Math.round(e.pageY - otop);
				} else if ("touchstart" == type) {
					var touch = e.originalEvent.touches[0]
							|| e.originalEvent.changedTouches[0];
					curr.x = Math.round(touch.pageX - oleft);
					curr.y = Math.round(touch.pageY - otop);

					// Move touchstart start position
					perc.x = (curr.x / canvas.width).toFixed(4);
					perc.y = (curr.y / canvas.height).toFixed(4);
					socket.emit('mousemove', {
						'uuid' : uuid,
						'x' : perc.x,
						'y' : perc.y,
						'drawing' : false,
						'id' : id
					});
				}
				drawing = true;
				prev.x = curr.x;
				prev.y = curr.y;
			});

	// move
	$('canvas').bind(
			'mousemove touchmove',
			function(e) {
				e.preventDefault();
				var oleft = img.offset().left;
				var otop = img.offset().top;
				var type = e.type;
				if ("mousemove" == type) {
					curr.x = Math.round(e.pageX - oleft);
					curr.y = Math.round(e.pageY - otop);
				} else if ("touchmove" == type) {
					var touch = e.originalEvent.touches[0]
							|| e.originalEvent.changedTouches[0];
					curr.x = Math.round(touch.pageX - oleft);
					curr.y = Math.round(touch.pageY - otop);
				}

				perc.x = (curr.x / canvas.width).toFixed(4);
				perc.y = (curr.y / canvas.height).toFixed(4);

				if (Math.sqrt(Math.pow(prev.x - curr.x, 2)
						+ Math.pow(prev.y - curr.y, 2)) > 8) {
					/*
					 * var p = {x1:prev.x, y1:prev.y, x2:curr.x, y2:curr.y}
					 * lines.push(p); draw(p); send(JSON.stringify(p)); prev =
					 * curr;
					 */
					socket.emit('mousemove', {
						'uuid' : uuid,
						'x' : perc.x,
						'y' : perc.y,
						'drawing' : drawing,
						'id' : id
					});
					
					// Draw a line for the current user's movement, as it is
					// not received in the socket.on('moving') event above
					if (drawing) {
						drawLine(prev.x + 1, prev.y, curr.x + 1, curr.y);

						// save percent points
						var prePercX = (prev.x / canvas.width).toFixed(4);
						var prePercY = (prev.y / canvas.height).toFixed(4);
						p = {
							x1 : prePercX,
							y1 : prePercY,
							x2 : perc.x,
							y2 : perc.y
						};
						lines.push(p);

						prev.x = curr.x;
						prev.y = curr.y;
					}
				}
			});

	// end
	doc.bind('mouseup mouseleave touchend touchcancel', function(e) {
		e.preventDefault();
		drawing = false;
	});
}

function drawLine(fromx, fromy, tox, toy){
	ctx.moveTo(fromx, fromy);
	ctx.lineTo(tox, toy);
	/*
	ctx.strokeStyle = 'red';
    ctx.lineWidth = "10";
    ctx.lineCap = "round";
    */
	ctx.stroke();
}

$(window).resize(function() {
	resetImgSizeSync();
});

function resetImgSizeSync() {
	$('.img-container-sync img').load(function() {
		$('.slide-canvas').width($('.img-container-sync').width());
		$('.slide-canvas').height($('.img-container-sync').height());
		$('.slide-canvas')[0].width = $('.img-container-sync').width();
		$('.slide-canvas')[0].height = $('.img-container-sync').height();
		resetStroke();
	});
	$('.slide-canvas').width($('.img-container-sync').width());
	$('.slide-canvas').height($('.img-container-sync').height());
	$('.slide-canvas')[0].width = $('.img-container-sync').width();
	$('.slide-canvas')[0].height = $('.img-container-sync').height();
	resetStroke();
}

function resetStroke() {
	if (canvas) {
		ctx = canvas.getContext("2d");
		ctx.strokeStyle = 'red';
	    ctx.lineWidth = "2";
	    ctx.lineCap = "round";
	} else {
		alert('Please set canvas first!');
	}
}

function preSlideSync() {
	var preSlide = eval(Number(getCurSlide()) - 1);
	gotoSlideSync(preSlide);
}

function nextSlideSync() {
	var nextSlide = eval(Number(getCurSlide()) + 1);
	gotoSlideSync(nextSlide);
}

function gotoSlideSync(slide) {
	// slide turning
	var preSlide = curSlide;
	var slideSum = slideUrls.length;
	if (slide <= 0) {
		slide = 1;
	} else if (slideSum < slide) {
		slide = slideSum;
	}
	curSlide = slide;
	
	// set img & canvas
	var imgHtml = '<img class="slide-img-' + (slide - 1) + '" alt="第1页" src="' + slideUrls[slide - 1] + '">';
	var canvasHtml = '<canvas class="slide-canvas-' + (slide - 1) + ' slide-canvas" style="width: 100%; height: 100%; border: 1px solid orange; position: absolute; left: 0px; top: 0px;">您的浏览器不支持画布！</canvas>';
	$('.img-container-sync').html(imgHtml + canvasHtml);
	canvas = $('.slide-canvas-' + (slide - 1))[0];
	resetImgSizeSync();
	img = $('.slide-img-' + (slide - 1));
	// bindCanvasEvent();
	
	/*
	 * $(".img-container-sync img").fadeOut(function() { $(this).attr("src",
	 * slideUrls[slide - 1]).fadeIn(); });
	 */
	$(".img-container-sync img").attr("src", slideUrls[slide - 1]);
	var percent = Math.ceil((curSlide / slideUrls.length) * 100);
	$('.select-page-selector').val(slide);
	$('.select-page-selector-sync').val(slide);
	$('.bottom-paging-progress .bar').width('' + percent + '%');

	// sync flip event to audience
	/*
	socket.emit('flip', {
		'uuid' : uuid,
		'page' : curSlide,
	});
	*/
}