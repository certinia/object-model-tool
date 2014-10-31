/*
 * Copyright (c) 2014, FinancialForce.com, inc
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 *   are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, 
 *      this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *      this list of conditions and the following disclaimer in the documentation 
 *      and/or other materials provided with the distribution.
 * - Neither the name of the FinancialForce.com, inc nor the names of its contributors 
 *      may be used to endorse or promote products derived from this software without 
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 *  THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 *  OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 *  OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * Author: Brad Slater (DevOps) financialforce.com
 * Version: 0.0.2
 *
 * This javascript/jquery file handles the layout logic for the object model charts
 * generated. Charts start by being drawn at position 0,0 and are dynamically layed out
 * by javascript. This object also handles the various click events and draw events for the
 * tables and draws/redraws the connector pipes.
 */

// full scope var to contain the layout object set.
var GlobalLayoutObject = {};

//full scope var to contain the current z-index.
var currentZIndex = 2;

/* init method to be called by whatever is running the logic.js file.
 *
 * input: an instantiated ChartLayoutObject e.g 'new ChartLayoutObject()';
 * (this allows you to override default methods with custom implementation)
 *
 * e.g:
 *
 * var layoutNew = new ChartLayoutObject();
 * layoutNew.layoutTables = function(callback){	
 * 	   //do nothing...
 * }
 */
function initialiseLayout(inputLayoutObject){

	//define the layout object.
	var layoutObject = inputLayoutObject;

	//set the global layout object to the layout object sent for future reference.
	GlobalLayoutObject = layoutObject;

	//create stubs.
	layoutObject.createUnreferencedTableStubs();

	//get the ordered table ids.
	var tableScore = layoutObject.scoreTables();

	//read then write table code to wrapper in the correct order
	var split = [];
	$.each(tableScore, function(index, value){
		var tableCode = $('#'+tableScore[index]);
		split.push(tableCode);
	});
	$(layoutObject.getContainerDivID()).append(split);

	//layout tables (has callback to then draw relationships)
	layoutObject.layoutTables(layoutObject.drawRelationships);

	//call jsPlumb refresh
	jsPlumb.repaintEverything();

	//make objects draggable
	layoutObject.makeTablesDraggable(layoutObject.dropEvent, layoutObject.getContainerDivID(), layoutObject.suspendLayout)

}

//////////////////////////////////// EVENT HANDLERS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

//function to handle the click of any table item (row, expand button, collapse button...)
function tableDivClicked(sender){
	//expand button click...
	if(sender.attr('class') == "expandButton"){

		//get parent .table id
		var tableId = sender.parent().attr('id');

		//turn off button if table is marked as hidden
		if(!$("#" + tableId).hasClass("hiddenTable")){
			
			//expand	
      		if(sender.text() == "+"){
      			sender.text("-");
      			//for all rows in table
      			var animateTargets = $("div", "#"+tableId).not(".expandButton").not(".hideButton").not(".hide");
      			animateTargets.each(function(index, value){
      				if(index == (animateTargets.length -1)){
      					//last animate so callbacks to refresh endpoints and layout.
      					GlobalLayoutObject.animateRow($(this), GlobalLayoutObject.layoutTablesWithoutAnimation, jsPlumb.repaintEverything, GlobalLayoutObject.redrawLocalEndpoints, false);
      				} else {
      					//no callbacks (blank)
      					GlobalLayoutObject.animateRow($(this), function(dm){}, function(){}, function(dm, bool){}, false);
      				}
      			});
      		//contract
      		} else if(sender.text() == "-"){
      			sender.text("+");
      			//for all rows in table
      			var animateTargets = $("div", "#"+tableId).not(".expandButton").not(".hideButton").filter(".hide");
      			animateTargets.each(function(index, value){
	   				if(index == (animateTargets.length -1)){
	   					//last animate so callbacks to refresh endpoints and layout.
      					GlobalLayoutObject.animateRow($(this), GlobalLayoutObject.layoutTablesWithoutAnimation, jsPlumb.repaintEverything, GlobalLayoutObject.redrawLocalEndpoints, false);
      				} else {
      					//no callbacks (blank)
      					GlobalLayoutObject.animateRow($(this), function(dm){}, function(){}, function(dm, bool){}, false);
      				}
	  			});
      		}

        }

	} else
	//hide button click
	if(sender.attr('class') == "hideButton") {

		//get parent .table id
		var tableId = sender.parent().attr('id');

		//targets are common so get here
		var animateTargets = $("div", "#"+tableId).not(".expandButton").not(".hideButton");
		
		//make sure expand is set to expand
		$("div", "#"+tableId).filter(".expandButton").text("+");

		//hide
		if(sender.text() == "_"){
      		sender.text("^");
      		animateTargets.each(function(index, value){
      			if(index == (animateTargets.length -1)){
      				//custom implementation of animate function:
      				$(this).animate({height:0},{progress: function(n, step, n){
        				GlobalLayoutObject.layoutTablesWithoutAnimation(jsPlumb.repaintEverything);
        				GlobalLayoutObject.redrawLocalEndpoints($(this).parent(), true);
        			}}, 200,  "linear", function() {
        			}).removeClass('hide');
       	 		
        			$(this).promise().done(function(){
        				GlobalLayoutObject.layoutTablesWithoutAnimation(jsPlumb.repaintEverything);
        				GlobalLayoutObject.redrawLocalEndpoints($(this).parent(), true);
					});
      			} else {
      				//custom implementation of animate function:
      				$(this).animate({height:0},{progress: function(n, step, n){
        			}}, 200,  "linear", function() {
        			}).removeClass('hide');
      			}
      		});
      		//mark table as a hidden table
      		$("#" + tableId).addClass("hiddenTable");
      	//show
      	} else if(sender.text() == "^"){
      		sender.text("_");
	    	animateTargets.each(function(index, value){
	    		if(index == (animateTargets.length -1)){
      				//custom implementation of animate function:
      				$(this).animate({height:30},{progress: function(n, step, n){
        				GlobalLayoutObject.layoutTablesWithoutAnimation(jsPlumb.repaintEverything);
        				GlobalLayoutObject.redrawLocalEndpoints($(this).parent(), false);
        			}}, 200,  "linear", function() {
        			}).removeClass('hide');
       	 		
        			$(this).promise().done(function(){
        				GlobalLayoutObject.layoutTablesWithoutAnimation(jsPlumb.repaintEverything);
        				GlobalLayoutObject.redrawLocalEndpoints($(this).parent(), false);
					});
      			} else {
      				$(this).animate({height:30},{progress: function(n, step, n){
        			}}, 200,  "linear", function() {
        			}).removeClass('hide');
	    		}
      		});
      		//remove hidden table class
      		$("#" + tableId).removeClass("hiddenTable");
      	}

	} else {
	//row
		GlobalLayoutObject.animateRow(sender, GlobalLayoutObject.layoutTablesWithoutAnimation, jsPlumb.repaintEverything, GlobalLayoutObject.redrawLocalEndpoints, false);
	}

}

////////////////////////////////// PROTOTYPE FUNCTIONS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

//define the ChartLayoutObject class constructor
function ChartLayoutObject() {};

//// SETUP

//add a method to return the containing div name
ChartLayoutObject.prototype.getContainerDivID = function(){
	return "#wrapper";
}

//add the logic to create the unreferenced table stubs.
ChartLayoutObject.prototype.createUnreferencedTableStubs = function(){

	// make an array to contain links that do not reference an existing table.
	var badReferences = [];

	// for each link class
	$(".Trel").each(function(i){
		//get the table id referenced.
		var relRef = $(".Trel").get(i).innerHTML;
		//check to see if a table with this id exists.
		if( !$('#' + relRef).length ) {
			//if this table hasn't already been spotted,
			if (badReferences.indexOf(relRef) == -1){
				//add the table to the bad table list.
				badReferences.push(relRef);
			}
		}
	});

	//create a stub table for each bad reference
	$.each(badReferences, function(index,value){
		//get the table id
		var item = badReferences[index];
		//add a new stub table with this id.
		$("#wrapper").append("<div class=\"table\" id=\"" + item + "\" style=\"background-color: #8CC63F; text-align:center;\"><span style=\"line-height: 56px;font-size: 14px; color: #fff; \">"+ item +"</span></div>");
	})

}

//add the logic to score tables based on their links to other tables
ChartLayoutObject.prototype.scoreTables = function(){

	//create an array to keep track of a tables 'score'.
	var tableScore = [];

	//for each table class.
	$(".table").each(function(i){

		//get the id.
		var tableName = $(this).attr('id');
			
		//calculate the number of references made to this table.
		var numberOfRefs = 0;
		$(".Trel").each(function(i){
			var relRef = $(".Trel").get(i).innerHTML;
			if (relRef == tableName){
				numberOfRefs += 1;
			}
		});

		//calculate the number of references this table makes.
		var numberOfRefsMade = $(".Trel","#" + tableName).length;

		//score the table.
		var score = (numberOfRefs - numberOfRefsMade);
			
		//if table is unassociated with other tables...
		if (numberOfRefsMade == 0 && numberOfRefs == 0){
			score = 10000;
		}
			
		//add score and information to the tableScore array
		tableScore.push([score, tableName, numberOfRefs, numberOfRefsMade]);

	});

	// sort the array by score (just a bubblesort with a bit of extra logic)
	var arrayLength = tableScore.length;
	for (var h = 0; h < arrayLength; h++) {
		for (var i = 0; i < (arrayLength -1); i++) {
    		var score = tableScore[i][0];
    		var score2 = tableScore[i+1][0];
    		var pair = tableScore[i];
    		var pair2 = tableScore[i+1];
    		if(score > score2){
    			tableScore[i] = pair2;
    			tableScore[i + 1] = pair;
    		}
    		//if score is equal then table with less refs to goes first.
    		if (score == score2){
    			var refsTo = tableScore[i][2];
    			var refsTo2 = tableScore[i+1][2];
    			if (refsTo > refsTo2){
    				tableScore[i] = pair2;
    				tableScore[i + 1] = pair;
    			}
    		}
		}
	}

	//create a final array and add just table names in order for simplicity.
	var finalTableArray = [];
	for (var i = 0; i < (arrayLength); i++) {
		finalTableArray.push(tableScore[i][1]);
	}

	//return the final arrangement
	return finalTableArray;

}

//add the layout logic to reposition the tables
//input: the function to call when done e.g. drawRelationships
ChartLayoutObject.prototype.layoutTables = function(callback){
	
	//vars to control layout
	var csstop = 40;
	var cssleft = 40;
	var maxEverHeight = 0;

	//for each table class
	$(".table").each(function(){

		//animate the table position and set csstop to the bottom of the tables new position.
    	var e = $(this);
    	e.animate({top: csstop, left: cssleft});
    	csstop += e.outerHeight() + 40;
   	 		
   	 	//set the spacers left position to the current css left.
   		$("#spacer").css({left: cssleft});

   		//set max ever height to csstop if csstop is larger.
   		if(csstop > maxEverHeight){
    		maxEverHeight = csstop;
    	}

    	//if the cutoff point is reached add 400 to cssleft and reset csstop.
    	if (csstop > 1000 /*(height cutoff point)*/){
    		cssleft += 400;
    		csstop = 40;
    	}

    	//set the spacers top to max ever height -1;
    	$("#spacer").css({top: maxEverHeight -1});
    	
	});

	//after layout is complete, call the callback function.
	$(".table").promise().done(function(){
		callback();
	});

}

//add the layout logic to reposition the tables (no animation)
//input: the function to call when done e.g. drawRelationships
ChartLayoutObject.prototype.layoutTablesWithoutAnimation = function(callback){
	
	//vars to control layout
	var csstop = 40;
	var cssleft = 40;
	var maxEverHeight = 0;

	//for each table class
	$(".table").each(function(){

		//animate the table position and set csstop to the bottom of the tables new position.
    	var e = $(this);
    	e.css({top: csstop, left: cssleft});
    	csstop += e.outerHeight() + 40;
   	 		
   	 	//set the spacers left position to the current css left.
   		$("#spacer").css({left: cssleft});

   		//set max ever height to csstop if csstop is larger.
   		if(csstop > maxEverHeight){
    		maxEverHeight = csstop;
    	}

    	//if the cutoff point is reached add 400 to cssleft and reset csstop.
    	if (csstop > 1000 /*(height cutoff point)*/){
    		cssleft += 400;
    		csstop = 40;
    	}

    	//set the spacers top to max ever height -1;
    	$("#spacer").css({top: maxEverHeight -1});
    	
	});

	//after layout is complete, call the callback function.
	$(".table").promise().done(function(){
		callback();
	});

}

//add logic to draw table relationships
ChartLayoutObject.prototype.drawRelationships = function(){
	
	/* for each link */	
	$(".Trel",".table div").each(function(i){

		//get vars needed to draw connections
   		var e = $(this);
   		var source = e.parent().parent().parent().attr('id');
   		var uID = e.attr("id");
   		var topval = e.parent().parent().position().top;
   		var target = e.html();

		/* jsplumb setup stuff */
		jsPlumb.registerEndpointType("example", {
 			paintStyle:{ fillStyle:"${color}"},
		});

		//set start point
		var from = jsPlumb.addEndpoint(source, { 
			type: "example",
  			data:{ color: 'rgb(160,160,160)'},
  			anchor:[ 1,  0 , 1, 0, -2, 0 + topval + 15, uID ],
		});

		//set end point
		var to = jsPlumb.addEndpoint(target, { 
			type: "example",
  			data:{ color: 'rgb(160,160,160)' },
			anchor:[ 0,  0 , -1, 0, 0, 15, "default"+target ],
		});

		//set connection
		var conn = jsPlumb.connect({
			paintStyle: {lineWidth:2,strokeStyle:'rgb(149,149,149)'},
 			source: from, 
 			target: to
		});
            
	});

}

//function to suspend layout operations
ChartLayoutObject.prototype.suspendLayout = function(){

	//rewrite layout functions to do nothing other than execute their callbacks
	this.layoutTablesWithoutAnimation = function(callback){ callback() }
	this.layoutTables = function(callback){ callback() }
	ChartLayoutObject.prototype.layoutTablesWithoutAnimation = function(callback){ callback() }
	ChartLayoutObject.prototype.layoutTables = function(callback){ callback() }

}

//function to make tables draggable
//input: drop event callback function
//input: container id
//input: refresh function
//input: refresh function callback
//input: suspend layout function
ChartLayoutObject.prototype.makeTablesDraggable = function(callback, containerId, suspendLayoutCallback ){
	
	$(".table").draggable({
       	opacity: 1,
       	scroll: true,
       	containment: containerId,
        stop: function(){
        	currentZIndex += 2;
        	//call refresh function with its callback
        	$("#spacer").css({left: $(this).css("left")});
    	    jsPlumb.repaintEverything();
        	callback();
        },  
        drag: function(e,ui){  

        	suspendLayoutCallback();

        	$(this).css("z-index", currentZIndex);  	
        	var thisTable = $(this);

        	//recss the local endpoints
        	var endpoints = jsPlumb.getEndpoints($(this).attr("id"));
			if(endpoints=== undefined){} 
			else {
				$.each(endpoints, function(index, value){
					//work on endpoints
					var elementUniqueClass = "._jsPlumb_endpoint_anchor_" + endpoints[index].anchor.getCssClass();
					var elementUniqueClass2 = "._jsPlumb_endpoint_anchor_default";
					$("body").find(elementUniqueClass).each(function(){
						if($(this).attr('id') === undefined){
							$(this).css("z-index", currentZIndex -1);
						}
					});
					$("body").find(elementUniqueClass2).each(function(){
						if($(this).attr('id') === undefined){
							$(this).css("z-index", currentZIndex -1);
						}
					});

				});
			}
    	    jsPlumb.repaintEverything();
    	}
	});

}

//blank table drop event to override.
ChartLayoutObject.prototype.dropEvent = function(){}

//// EVENTS

/* function to animate a row to expanded or collapsed based on its state
 *
 * input: 
 *		the object clicked on (sender, this, etc...),
 *		refresh function (needs a callback) (or nothing) (nothing is function(doesntMatter){}), 
 *		redraw function (needs .table and hidden as input) (or nothing) (nothing is function(doesntMatter, bool){})
 *		redraw function hidden (boolean)
 */
ChartLayoutObject.prototype.animateRow = function(obj, refreshCallback, refreshCallbackCallback, redrawEndpointCallback, redrawEndpointHidden ){
	
	if(obj.hasClass('hide')) {
        
        obj.animate({height:30},{progress: function(n, step, n){
        	refreshCallback(refreshCallbackCallback);
        	redrawEndpointCallback(obj.parent(), redrawEndpointHidden);
        }}, 200,  "linear", function() {
        }).removeClass('hide');
        		
        obj.promise().done(function(){
        	refreshCallback(refreshCallbackCallback);
        	redrawEndpointCallback(obj.parent(), redrawEndpointHidden);
		});
    
    } else { 
      		
      	obj.css('height', 'auto');
      	var autoHeight = obj.height();
      	
      	obj.height(30).animate({height:autoHeight},{progress: function(n, step, n){
       		refreshCallback(refreshCallbackCallback);
       		redrawEndpointCallback(obj.parent(), redrawEndpointHidden);
       	}}, 200,  "linear", function() {
       	}).addClass('hide');
       	
       	obj.promise().done(function(){
       		refreshCallback(refreshCallbackCallback);
       		redrawEndpointCallback(obj.parent(), redrawEndpointHidden);
		});
       	
   	} 

}

// function to redraw the endpoints on a table
// input .table object, boolean hidden
ChartLayoutObject.prototype.redrawLocalEndpoints = function(tableDiv, hidden){

	//set the amount to add or subtract from endpoint height
	var addition = 15;
	if(hidden){
		addition = -15;
	}

	//get the tables id
	var objid = tableDiv.attr('id');

	//get all endpoints
	var endpoints = jsPlumb.getEndpoints(objid);
	if(endpoints=== undefined){
		//do nothing
	} else {
		//redraw each endpoint to its correct position.
		$.each(endpoints, function(index, value){
			var uID = endpoints[index].anchor.getCssClass();
			if(uID != ("default"+objid)){
				var topval = $("#"+uID).parent().parent().position().top;
				endpoints[index].setAnchor([ 1,  0 , 1, 0, -2, 0 + topval + addition, uID ]);
			} 
		});
	}

}
