(function($) {

	skel.breakpoints({
		xlarge: '(max-width: 1680px)',
		large: '(max-width: 1366px)',
		medium: '(max-width: 1080px)',
		small: '(max-width: 768px)',
		xsmall: '(max-width: 736px)',
		xxsmall: '(max-width: 480px)'
	});

	$.fn.disableSelection = function() { return $(this).css('user-select', 'none').css('-khtml-user-select', 'none').css('-moz-user-select', 'none').css('-o-user-select', 'none').css('-webkit-user-select', 'none'); }
	$.fn.panel=function(userConfig){if(this.length==0)return $this;if(this.length>1){for(var i=0;i<this.length;i++)$(this[i]).panel(userConfig);return $this}var $this=$(this),$body=$("body"),$window=$(window),id=$this.attr("id"),config;config=$.extend({delay:0,hideOnClick:false,hideOnEscape:false,hideOnSwipe:false,resetScroll:false,resetForms:false,side:null,target:$this,visibleClass:"visible"},userConfig);if(typeof config.target!="jQuery")config.target=$(config.target);$this._hide=function(event){if(!config.target.hasClass(config.visibleClass))return;if(event){event.preventDefault();event.stopPropagation()}config.target.removeClass(config.visibleClass);window.setTimeout(function(){if(config.resetScroll)$this.scrollTop(0);if(config.resetForms)$this.find("form").each(function(){this.reset()})},config.delay)};$this.css("-ms-overflow-style","-ms-autohiding-scrollbar").css("-webkit-overflow-scrolling","touch");if(config.hideOnClick){$this.find("a").css("-webkit-tap-highlight-color","rgba(0,0,0,0)");$this.on("click","a",function(event){var $a=$(this),href=$a.attr("href"),target=$a.attr("target");if(!href||href=="#"||href==""||href=="#"+id)return;event.preventDefault();event.stopPropagation();$this._hide();window.setTimeout(function(){if(target=="_blank")window.open(href);else window.location.href=href},config.delay+10)})}$this.on("touchstart",function(event){$this.touchPosX=event.originalEvent.touches[0].pageX;$this.touchPosY=event.originalEvent.touches[0].pageY});$this.on("touchmove",function(event){if($this.touchPosX===null||$this.touchPosY===null)return;var diffX=$this.touchPosX-event.originalEvent.touches[0].pageX,diffY=$this.touchPosY-event.originalEvent.touches[0].pageY,th=$this.outerHeight(),ts=$this.get(0).scrollHeight-$this.scrollTop();if(config.hideOnSwipe){var result=false,boundary=20,delta=50;switch(config.side){case"left":result=diffY<boundary&&diffY>-1*boundary&&diffX>delta;break;case"right":result=diffY<boundary&&diffY>-1*boundary&&diffX<-1*delta;break;case"top":result=diffX<boundary&&diffX>-1*boundary&&diffY>delta;break;case"bottom":result=diffX<boundary&&diffX>-1*boundary&&diffY<-1*delta;break;default:break}if(result){$this.touchPosX=null;$this.touchPosY=null;$this._hide();return false}}if($this.scrollTop()<0&&diffY<0||ts>th-2&&ts<th+2&&diffY>0){event.preventDefault();event.stopPropagation()}});$this.on("click touchend touchstart touchmove",function(event){event.stopPropagation()});$this.on("click",'a[href="#'+id+'"]',function(event){event.preventDefault();event.stopPropagation();config.target.removeClass(config.visibleClass)});$body.on("click touchend",function(event){$this._hide(event)});$body.on("click",'a[href="#'+id+'"]',function(event){event.preventDefault();event.stopPropagation();config.target.toggleClass(config.visibleClass)});if(config.hideOnEscape)$window.on("keydown",function(event){if(event.keyCode==27)$this._hide(event)});return $this};

	$(function() {

		var $window = $(window),
			$body = $('body'),
			$header = $('#header'),
			$footer = $('#footer'),
			$sidebar = $header.add($footer),
			$main = $('#main'),
			$items = $('.item');

		// Disable animations/transitions until the page has loaded.
			$body.addClass('is-loading');

			$window.on('load', function() {
				$body.removeClass('is-loading');
			});

		if ($body.hasClass('is-demo')) {

			var $main = $('#demo-main'),
				$iframe = $('#demo-iframe'),
				$download = $('.button.download.free'),
				mobile = skel.vars.mobile;

			// Mobile adjustments.
				if (mobile) {

					if ($iframe.data('responsive') != 1) {

						var $iframeAlt,
							$iframeAlt_button;

						// Iframe (alt).
							$iframeAlt =
								$(
									'<div id="demo-iframe-alt">' +
										'<div class="inner">' +
											'<ul class="actions">' +
												'<li><span class="button">Open in New Window</a></li>' +
											'</ul>' +
										'</div>' +
										'<div class="bg" style="background-image: url(\'' + $iframe.data('thumbnail') + '\')"></div>' +
									'</div>'
								)
									.insertAfter($iframe);

						// Button.
							$iframeAlt_button = $iframeAlt.find('.button');

							$iframeAlt_button
								.on('click', function(event) {

									event.preventDefault();
									event.stopPropagation();

									window.open($iframe.attr('src'));

								});

						// Remove original iframe.
							$iframe.remove();

					}

					$main
						.css('top', 0)
						.css('position', 'relative');

				}

			// Window.
				$window
					.resize(function() {
						$main.css('height', $window.height() - 110);
					})
					.trigger('resize');

			// Modal.
				var $modal = $(
						'<div id="demo-modal">' +
							'<section>' +
								'<h2>Downloading</h2>' +
								'<p>Like what we\'re doing here? Show your support!</p>' +
								'<ul class="actions vertical">' +
									'<li><ul class="social"><li class="facebook"><div class="fb-like" data-href="http://templated.co" data-send="false" data-layout="button_count" data-width="100" data-show-faces="true"></div></li><li class="twitter"><a href="https://twitter.com/share" class="twitter-share-button" data-url="http://templated.co" data-text="TEMPLATED = awesome CSS, HTML5, and responsive site template freebies." data-count="horizontal">Tweet</a><script type="text/javascript" src="http://platform.twitter.com/widgets.js"></script></li><li class="google-plus"><div class="g-plusone" data-size="medium" data-href="http://templated.co"></div></li></ul></li>' +
									'<li><a href="http://twitter.com/templatedco" class="button offsite">Follow @templatedco</a></li>' +
								'</ul>' +
								'<div class="closer">&nbsp;</div>' +
							'</section>' +
						'</div>'
					),
					$modal_inner = $modal.find('section'),
					$modal_closer = $modal.find('.closer'),
					$modal_timeoutId,
					$modal_locked;

				$modal
					.hide()
					.appendTo($body)
					.disableSelection()
					.on('click', function() {
						$modal._close();
						return true;
					});

				$modal_inner
					.on('click', function(e) {
						e.stopPropagation();
					});

				$modal
					.on('click', function(e) {
						$modal._close();
						return false;
					});

				$modal_closer
					.on('click', function(e) {
						$modal._close();
						return false;
					});

				$modal._open = function(url) {
					window.clearTimeout($modal_timeoutId);
					$modal_locked = true;
					$modal
						.fadeTo('fast', 1.0, function() {
							if (typeof FB !== 'undefined'
							&&	typeof FB.XFBML !== 'undefined')
								FB.XFBML.parse();
							$modal_locked = false;
							$modal_timeoutId = window.setTimeout(function() {
								window.location.href = url;
							}, 1500);
						});
				};

				$modal._close = function() {
					if (!$modal.is(':visible'))
						return false;

					window.clearTimeout($modal_timeoutId);
					$modal_locked = true;
					$modal
						.fadeTo('fast', 0, function() {
							$modal.hide();
							$modal_locked = false;
						});
				};

				$download
					.on('click', function(e) {

						if (!mobile) {

							$modal._open($(this).attr('href'));
							return false;

						}

					});

				$window
					.on('keydown', function(e) {

						if (e.keyCode == 27)
							$modal._close();

					});

		}
		else {

			// Sidebar Panel.

				// Panel.
					$('<div id="sidebarPanel"><a href="#sidebarPanel" class="closer"></a></div>')
						.appendTo($body)
						.panel({
							delay: 500,
							side: 'left',
							target: $body,
							hideOnSwipe: true,
							hideOnClick: true,
							resetScroll: true,
							visibleClass: 'is-sidebarPanel-visible'
						});

				// Titlebar.
					$('<div id="titleBar"><a href="#sidebarPanel" class="toggle"></a><span class="logo">藏書閣</span></div>')
						.appendTo($body);

				// Events.

					var $sidebarPanel = $('#sidebarPanel');

					skel
						.on('+medium', function() {

							$header.appendTo($sidebarPanel);
							$footer.appendTo($sidebarPanel);

						})
						.on('-medium', function() {

							$header.insertBefore($main);
							$footer.insertAfter($main);

						});

			// Items.
				$items
					.css('cursor', 'pointer')
					.on('click', function(e) {
						window.location.href = $(this).find('a').first().attr('href');
						return false;
					});

			// Sidebar.
				$window.load(function() {
					var sh, wh, st;

					if ($window.scrollTop() == 1)
						$window.scrollTop(0);

					$window
						.on('scroll', function() {
							if (skel.breakpoint('medium').active
							||	skel.vars.touch) {
								$sidebar
									.data('fixed', 0)
									.css('position', 'absolute')
									.css('top', 0);

								return;
							}

							var x, y;

							x = Math.max(sh - wh, 0);
							y = Math.max(0, $window.scrollTop() - x);

							if ($sidebar.data('fixed') == 1) {
								if (y <= 0)
									$sidebar
										.data('fixed', 0)
										.css('position', 'absolute')
										.css('top', 0);
							}
							else {
								if (y > 0)
									$sidebar
										.data('fixed', 1)
										.css('position', 'fixed')
										.css('top', -1 * x);
							}
						})
						.on('resize', function() {

							sh = $footer.outerHeight();
							wh = $window.height();
							$main.css('min-height', sh + 10);
							$window.trigger('scroll');

						})
						.trigger('resize');
				});

		}

		// Offsite.
			$('a.offsite').attr('target', '_blank');

		// Share.
			(function(d, s, id) { var js, fjs = d.getElementsByTagName(s)[0]; if (d.getElementById(id)) {return;} js = d.createElement(s); js.id = id; js.src = "../connect.facebook.net/en_US/all.js#xfbml=1"; fjs.parentNode.insertBefore(js, fjs); }(document, 'script', 'facebook-jssdk'));
			(function() { var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true; po.src = '../apis.google.com/js/plusone.js'; var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s); })();

	});

})(jQuery);