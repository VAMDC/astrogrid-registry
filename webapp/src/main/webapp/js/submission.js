/**
 *
 */
function Xsubmission (oAttributes) { 
	this.attributes = oAttributes;
	
	this.action = this.attributes['action'];
	
	this.method = this.attributes['method'];
	this.replace = this.attributes['replace'];
	this.parent = null;
	this.element = document.getElementById(this.attributes['id']);
	this.element.submission = this;
	this.element.addEventListener('click',this.sendHandler,false);
}	

Xsubmission.prototype.parseUrl = function(sUrl) {
	myRegexp = new RegExp("(.+)\:\/\/(?:(?:(.+)\:(.+)\@)?([^\/]+))(?:\/([^\?]+))?(?:([^#]+))?(?:\#(.*))?");	

	if (sUrl.match(myRegexp)) {
		aUrl = sUrl.split(myRegexp);
		
		aUrl.shift();
		aUrl.pop();
	
		aUrl['scheme'] = aUrl[0];
	    aUrl['user'] = 	aUrl[1]; 		
	    aUrl['pass'] = 	aUrl[2];
	    aUrl['host'] = 	aUrl[3];
	    aUrl['path'] = 	aUrl[4];
	    
	    aUrl[5] = aUrl[5].substring(1,aUrl[5].length); // enleve le ? parasite (bug expression reguliere)
	    aUrl['query'] =  aUrl[5];
	    
	    aUrl['fragment'] = 	aUrl[6];
    
	}
   return aUrl;
}

Xsubmission.prototype.sendHandler= function(evt) {
	var httpRequest = new XMLHttpRequest(); 
	var submission = evt.currentTarget.submission;
	var responseReplace = submission.replace;
	
	//var parsedUrl = Xsubmission.parseUrl(submission.action);
	
	document.dispatchEvent(Xevents['xforms-submit']);
	
	httpRequest.onreadystatechange = function() { 
		if (httpRequest.readyState == 4) {
			switch (httpRequest.status) {
				case 200:
					switch (responseReplace) {
						case 'all': 
							document.write(httpRequest.responseText);
							document.dispatchEvent(Xevents['xforms-submit-done']);	
							break;
			
						case 'instance':
							break;
				
						case 'none':
							break;

						case undefined:
						case null:
							alert(httpRequest.responseText);
							document.dispatchEvent(Xevents['xforms-submit-done']);	
							break;
							
						default:
							var elementReplace = document.getElementById(responseReplace);
							if (elementReplace) {
								elementReplace.innerHTML=httpRequest.responseText;
								document.dispatchEvent(Xevents['xforms-submit-done']);	
							}
					}
					break;
				default:
					myDebug.addLog('error','HTTP' + httpRequest.status);
					document.dispatchEvent(Xevents['xforms-submit-error']);
				}
		}
	}
	
		switch (submission.method) {
			case 'get':
				httpRequest.open('GET', submission.action+'?'+submission.parent.asXform());
				httpRequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
				httpRequest.setRequestHeader('Referer',window.location.href);
				httpRequest.send(null); 
				break;

			case 'form-data-post':
				// multipart not implemented yet
				httpRequest.open('POST',  submission.action); 
				httpRequest.setRequestHeader('Content-Type','multipart/form-data');
				httpRequest.setRequestHeader('Referer',window.location.href);
  				httpRequest.send(submission.parent.asXform());
				break;

			case 'urlencoded-post':
				// this is a deprecated mehtod
				httpRequest.open('POST',  submission.action); 
				httpRequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
				httpRequest.setRequestHeader('Referer',window.location.href);
  				httpRequest.send(submission.parent.asXform());
  				
  				
				break;
				
			case 'multipart-post':
				// multipart not implemented yet
				httpRequest.open('POST', submission.action);
				httpRequest.setRequestHeader('Content-Type','multipart/related');
				httpRequest.setRequestHeader('Referer',window.location.href);
				httpRequest.send(submission.parent.asMultipart());
				break;

			case 'post':
			default:
				httpRequest.open('POST',  submission.action); 
				httpRequest.setRequestHeader('Content-Type','application/xml');
				httpRequest.setRequestHeader('Referer',window.location.href);
  				httpRequest.send(submission.parent.asXml()); 
  				
		}


}

