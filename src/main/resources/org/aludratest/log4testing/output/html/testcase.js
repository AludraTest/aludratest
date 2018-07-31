function changeBtnText(id) {
	var showHideBtn = $("#shbteststepgroup" + id);
	var showText = '[+]';
	var hideText = '[-]';
	if (showHideBtn.text().indexOf(showText) >= 0) {
		showHideBtn.text(hideText);
	} else {
		showHideBtn.text(showText);
	}
};
function toggle(id) {
	$("#teststepgroup" + id).toggle();
	changeBtnText(id);
};
