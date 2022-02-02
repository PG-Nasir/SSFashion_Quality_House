window.onload = () => {
	document.title = "File Upload";
	document.getElementById('files').addEventListener('change', onFileSelect, false);
	document.getElementById('uploadButton').addEventListener('click', startUpload, false);
	//document.getElementById('find').addEventListener('click', find1, false);
}


//To log everything on console
function debug(s) {
	var debug = document.getElementById('debug');
	if (debug) {
		debug.innerHTML = debug.innerHTML + '<br/>' + s;
	}
}

//Will be called when upload is completed
function onUploadComplete(e) {
	totalUploaded += document.getElementById('files').files[filesUploaded].size;
	filesUploaded++;
	// debug('complete ' + filesUploaded + " of " + fileCount);
	//  debug('totalUploaded: ' + totalUploaded);
	if (filesUploaded < fileCount) {
		uploadNext();
	} else {
		var bar = document.getElementById('bar');
		bar.style.width = '100%';
		bar.innerHTML = '100% complete';
		//notification();
	}
}

function notification() {
	if (percentage == 100) {
		alert("Files Uploaded")
	}
}

//Will be called when user select the files in file control
function onFileSelect(e) {
	var bar = document.getElementById('bar');
	bar.style.width = 0 + '%';
	bar.innerHTML = 0 + ' % complete';

	var files = e.target.files; // FileList object
	var output = [];
	fileCount = files.length;
	totalFileLength = 0;
	for (var i = 0; i < fileCount; i++) {
		var file = files[i];
		output.push(file.name, ' (', file.size, ' bytes, ', file.lastModifiedDate.toLocaleDateString(), ')');
		output.push('<br/>');
		debug('add ' + file.size);
		totalFileLength += file.size;
	}
	// document.getElementById('selectedFiles').innerHTML = output.join('');
	debug('totalFileLength:' + totalFileLength);
}

//This will continueously update the progress bar
function onUploadProgress(e) {
	if (e.lengthComputable) {
		var percentComplete = parseInt((e.loaded + totalUploaded) * 100 / totalFileLength);
		var bar = document.getElementById('bar');
		bar.style.width = percentComplete + '%';
		bar.innerHTML = percentComplete + ' % complete';
		console.log(" bar prog " + percentComplete)

		percentage = percentComplete;

		console.log(" percentage prog " + percentage)
	} else {
		debug('unable to compute');
	}
}

//the Ouchhh !! moments will be captured here
function onUploadFailed(e) {
	alert("Error uploading file");
}

//Pick the next file in queue and upload it to remote server
function uploadNext() {

	var departments=[];
	$('#sectionSearch :selected').each(function(i, selectedElement) {
		departments[i]=$(selectedElement).val();
		i++;
	});
	
	for (var i = 0; i < departments.length; i++) {
		console.log(" depts "+departments[i])
	}
	if (departments.length==0) {
		departments[0]="0"
	}

	var heading=$('#heading').val()
	var textbody=$("#textbody").val()
	var userid=$("#userId").val()
	if(heading!=""){
		var xhr = new XMLHttpRequest();
		var fd = new FormData();
		var file = document.getElementById('files').files[filesUploaded];
		fd.append("multipartFile", file);
		xhr.upload.addEventListener("progress", onUploadProgress, false);
		xhr.addEventListener("load", onUploadComplete, false);
		xhr.addEventListener("error", onUploadFailed, false);

	//	let url = new URL('save-notice');
		
		//url.searchParams.set(heading, textbody);

		xhr.open("POST", "save-notice/"+heading+"/"+departments+"/"+textbody+"/"+userid);
		debug('uploading ' + file.name);
		console.log(" file name "+fd)
		xhr.send(fd);
	}else{
		alert("Insert Notice Heading")
	}


}

//Let's begin the upload process

function startUpload() {

	totalUploaded = filesUploaded = 0;
	uploadNext();

}


function download(v) {
	console.log("v "+v)
	var filename=$(v).attr('data-file');
	console.log(" file name "+filename)

	var file = decodeURIComponent(filename);
	var user = $("#userId").val();
	var xhr = new XMLHttpRequest();
	xhr.open("POST", "attachmetndownload/" + file);
	xhr.responseType = 'arraybuffer';
	xhr.onload = function () {
		if (this.status === 200) {
			var filename = "";
			var disposition = xhr.getResponseHeader('Content-Disposition');
			if (disposition && disposition.indexOf('attachment') !== -1) {
				var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
				var matches = filenameRegex.exec(disposition);
				if (matches != null && matches[1]) {
					filename = matches[1].replace(/['"]/g, '');
				}
			}
			var type = xhr.getResponseHeader('Content-Type'); var blob = typeof File === 'function' ? new File([this.response], filename, { type: type }) : new Blob([this.response], { type: type });
			if (typeof window.navigator.msSaveBlob !== 'undefined') {
				// IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created. 
				// These URLs will no longer resolve as the data backing the URL has been freed."
				window.navigator.msSaveBlob(blob, filename);
			} else {
				var URL = window.URL || window.webkitURL;
				var downloadUrl = URL.createObjectURL(blob); if (filename) {
					// use HTML5 a[download] attribute to specify filename
					var a = document.createElement("a");
					// safari doesn't support this yet
					if (typeof a.download === 'undefined') {
						window.location = downloadUrl;
					} else {
						a.href = downloadUrl;
						a.download = filename;
						document.body.appendChild(a);
						a.click();
					}
				} else {
					window.location = downloadUrl;
				}
				setTimeout(function () { URL.revokeObjectURL(downloadUrl); }, 100); // cleanup
			}
		}
	};
	xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');

	xhr.send($.param({

	}));
}




function redirectPage(){
   // window.open.href=pageName;
    
    
    $.ajax({
		type: 'POST',
		dataType: 'json',
		url: './previousnoticeopen',
		data:{
			
		},
		success: function (data) {
			if(data=='yes'){
				var url = "loadpreviousnotice";
        		window.open(url, '_blank');
			}
		},
	});
}


function search(){
	  if ($("#searchnotice").val()!=0) {
		  var body=  $("#searchnotice").find('option:selected').attr("data-details");
		  var header= $("#searchnotice option:selected").text();
		  console.log(" body "+body)
		  $("#heading").val(header)
		  $("#textbody").val(body)
		  document.getElementById('edit').hidden=false;
		  document.getElementById('uploadButton').hidden=true;
	}
	
}

var divv="";
var rowIndex=0;
$("table tbody tr").mouseover(
		  function () {
			  
			   
			
			
		   // $(this).css("background","yellow");
		  } ,function () {
			  var rowIndex = $(this).closest('tr').index()+1;
			  console.log(" mouse out ")
			  
			   var header= $(this).attr('data-header');
			 console.log("mheader "+header)
			 $("#header-"+rowIndex).text(header)
			 
			var body=$(this).attr('data-body');
			 $("#bodytext-"+rowIndex).text(body)
			console.log(" mbody text "+body)
			
			
			var divshowing=$("#mydiv-"+rowIndex);
			 document.getElementById('mydiv-'+rowIndex).hidden=false;
			  
			 // document.getElementById('mydiv-'+rowIndex).hidden=true;
			 // divv=document.getElementById("mydiv");
			 // divv.style.removeProperty("transform:rotate(360deg);transition: 1s;");
			 // document.getElementById("mydiv").style.transform=" rotate(360deg)";
		   // $(this).css("background","");
		  }
		);

$("table tbody tr").mouseout(
		  function () {
			  var rowIndex = $(this).closest('tr').index()+1;
			  console.log(" mouse out "+rowIndex)
			  document.getElementById("header-"+rowIndex).style.fontWeight = "Bold";
			  document.getElementById('mydiv-'+rowIndex).hidden=true;
				 
		  }); 
