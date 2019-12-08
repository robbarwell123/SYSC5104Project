/**
 * These functions are used to draw the map panel on the Sim Visualizer page to display results on the map.
 */
function TextPanel()
{
	var sContentLoc="";
	var sID="Text"+Math.random();
	
	var myText;
	
	function Render(){}

	Render.newPanel = function () {	
		var myText=d3.select(sContentLoc).append("DIV")
			.attr("id",sID)
			.attr("class","TEXTPANEL");
						
		// Subscribes to data updates from the player
		SubscribeToData(sID,this.update);
		Render.update(myLog[myFrames[iCurrFrame]]);
				
		return Render;
	};
	
	Render.update = function(myData)
	{		
		var myTextContent="";
		for(myLogEntry of myData)
		{
			try
			{
				if(myLogEntry.data.hasOwnProperty("message"))
				{
					myTextContent+=myLogEntry.name+": "+myLogEntry.data.message+"<BR>";
				}
			}catch(errEntry)
			{
				console.log("Unknown entry log error: "+errEntry);
			}
		}

		$("#"+sID).html(myTextContent);
		
		return Render;
	}
	
	Render.remove = function()
	{
		UnsubscribeToData(sID);
		d3.select("#"+sID).remove();
		return null;
	}
	
	Render.canvas = function(sValue) {
		if(!arguments.length) return sContentLoc;
		sContentLoc=sValue;
		return Render;
	};

	Render.id = function(sValue) {
		if(!arguments.length) return sID;
		sID=sValue;
		return Render;
	};
	
	return Render;
}