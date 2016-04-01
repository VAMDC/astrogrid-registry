function Datetime() {
	
	
	
}

Datetime.prototype.now = function () {
	
	this.oNow = new Date();            // create new date object 'DTnow'   
	this.Year  = this.oNow.getFullYear();       // years since 1900 (maybe! - see below)
	this.Month = this.oNow.getMonth();      // month-of-year field (0-11)
	this.Daynumber  = this.oNow.getDay();        // day-of-week field (0-6)
	this.Day = this.oNow.getDate();       // day-of-month field (1-31)
	this.Hour  = this.oNow.getHours();      // hours field (0-23)
	this.Mins  = this.oNow.getMinutes();    // minutes field (0-59)
	this.Secs  = this.oNow.getSeconds();    // seconds field (0-59)

	// Tidy up, zero-pad, etc, some of the variables
	if (this.Day < 10) this.Day = "0" + this.Day;
	if (this.Month < 10) this.Month = "0" + this.Month; 

	if (this.Hour < 10 ) this.Hour = "0" + this.Hour; 
	if (this.Mins < 10 ) this.Mins = "0" + this.Mins; 
	if (this.Secs < 10)  this.Secs = "0" + this.Secs; 

	return this.Year + '-' + this.Month + '-' + this.Day + 'T' + this.Hour + ':' + this.Mins + ':' + this.Secs;
}

