var Xactions = {
	
	_reset: function () {
		document.dispatchEvent(Xevents['xforms-reset']);
	},

	resetHandler: function (evt) {	
		var o = evt.target.control;
		Xactions._reset();
	},
	
	_recalculate: function () {
		xforms.childModels[0]._recalculate();
	},

	recalculateHandler: function (evt) {	
		var o = evt.target;
		Xactions._recalculate();
	},


	_rebuild: function() {
		xforms.childModels[0]._rebuild;
	},

	rebuildHandler: function (evt) {	
		var o = evt.target;
		Xactions._rebuild();
	},

	_refresh: function() {
		xforms.childModels[0]._refresh();
	},
	
	refreshHandler: function (evt) {	
		var o = evt.target;
		Xactions._rebuild();
	},

	_revalidate: function () {
		xforms.childModels[0]._revalidate();
	},

	revalidateHandler: function (evt) {	
		var o = evt.target;
		Xactions._revalidate();	
	},
	
	_delete: function () {	
		
	},

	deleteHandler: function (evt) {	
		var o = evt.target;
		evt.stopPropagation();
		evt.preventDefault();
		var oXdelete = evt.currentTarget.xdelete;
		oXdelete.parent.xdelete(oXdelete);
		o.dispatchEvent(Xevents['xforms-delete']);
	},
	
	_insert: function () {	
		
		
	},

	insertHandler: function (evt) {	
		var o = evt.target;
		evt.stopPropagation();
		evt.preventDefault();
		var oXinsert = evt.currentTarget.xinsert;
		oXinsert.parent.xinsert(oXinsert);
		o.dispatchEvent(Xevents['xforms-insert']);
	},
	
	
	_dispatch : function (oAttributes) {
		var target = document.getElementById(oAttributes['target']);
		target.dispatchEvent(Xevents[oAttributes['name']]);
	},
		
	dispatchHandler: function (evt) {
		var target = evt.target;
		target.dispatchEvent(Xevents[target.control.attributes['name']]);
	},

	_setvalue: function(oAttributes,oValue) {
		
	},
	
	setvalueHandler: function (evt) {
		var o = evt.target;
	}

};