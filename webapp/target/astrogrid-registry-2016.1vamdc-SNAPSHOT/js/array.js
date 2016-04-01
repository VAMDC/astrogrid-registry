Array.prototype.merge = function() {
	for(var i=0; i<arguments.length;i++) {
		for(var j=0; j<arguments[i].length;j++) {
			this[this.length] = arguments[i][j];
		}
	}	
}

Array.prototype.inArray = function(oVal) {
	for(var i=0;i<this.length;i++) {
		if (oVal==this[i]) return true;
	}
	return false;
}