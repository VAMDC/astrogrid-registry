include('debug.js');
include('array.js');
include('datetime.js');
include('md5.js');
include('xpath.js');
include('xpath-xforms.js');
include('xml-serializer.js');
include('dom3-xpath.js');
include('dom2.js');
include('dom2-drag.js');
include('dom2-core.js');
include('core.js');
include('values.js');
include('utf8.js');
include('xmlextras.js');
include('submission.js');
include('datatypes.js');
include('bind.js');
include('switch.js');
include('repeat.js');
include('hint.js');
include('actions.js');
include('httprequest.js');


function include(sFile) {
	document.write('<script type="text/javascript" src="js/'+sFile+'"></script>'); 
}
