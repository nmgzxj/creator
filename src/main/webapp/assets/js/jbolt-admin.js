//定义引入界面是否启用pjax
var mainPjaxContainerId='jbolt-container';
var mainPjaxContainer='#jbolt-container';

;(function($) {
    $.fn.extend({
        toJsonObject : function() {
            var o = {};
            var a = this.serializeArray();
            $.each(a, function() {
                if (o[this.name]) {
                    if (!o[this.name].push) {
                        o[this.name] = [ o[this.name] ];
                    }
                    o[this.name].push(this.value || '');
                } else {
                    o[this.name] = this.value || '';
                }
            });
            return o;
        }
    });

})(jQuery)


;(function($) {
    $.fn.extend({
        toggleContent : function(content) {
        	var html=this.html();
        	if(html&&html>0){
        		this.html("");
        	}else{
        		this.html(content);
        	}
        	
        }
    });

})(jQuery)


/**
 * 生成随机ID
 */
function randomId(){
	var ran=Math.random();
	ran=ran.toString().replace(".","");
	return new Date().getTime()+ran;
}

/**
 * switchBtn enableBtn
 */
var SwitchBtn={
		  init:function(parentId){
			  var that=this;
			  var parentId=parentId?("#"+parentId):"body";
			  var btns=$(parentId).find("img[data-switchbtn]");
			  if(btns&&btns.length>0){
				  var switchIt=function(_btn){
					  var url=_btn.data("url");
					var src=_btn.attr("src");
					var hander=_btn.data("handler");
					LayerMsgBox.loading("正在执行...",10000);
					$.ajax({
						type:"post",
						url:url,
						timeout : 10000, //超时时间设置，单位毫秒
						dataType:"json",
						success:function(data){
							if(data.state=="ok"){
								LayerMsgBox.success("操作成功",1000);
								var on=src.indexOf("off")!=-1;
								if(src.indexOf("off")!=-1){
									src=src.replace("off","on");
								}else{
									src=src.replace("on","off");
								}
								_btn.attr("src",src);
								if(hander){
									var exe=eval(hander);
									if(exe){
										exe(_btn,on);
									}
								}
							}else{
								LayerMsgBox.alert(data.msg,2);
							}
						},
						error:function(){
							LayerMsgBox.alert("操作失败",2);
						}
					});
				  }
				  btns.each(function(){
					  var _btn=$(this);
					  _btn.unbind("click").on("click",function(e){
						  e.preventDefault();
						  e.stopPropagation();
							var url=_btn.data("url");
							var confirm=_btn.data("confirm");
							if(url){
								if(confirm){
									LayerMsgBox.confirm(confirm,function(){
										switchIt(_btn);
									});
								}else{
									switchIt(_btn);
								}
								
							}else{
								LayerMsgBox.alert("组件未设置URL地址",2);
							}
					  })
				  });
			  }
			  
		  }
}


var DragScrollUtil={
		init:function(id){
			var drag = function drag(){
			    this.dragWrap = $("#"+id);
			   this.init.apply(this,arguments);
			  };
			   drag.prototype = {
			     constructor:drag,
			     _dom : {},
			     _x : 0,
			     _y : 0,
			     _top :0,
			     _left: 0,
			     move : false,
			     down : false,
			     init : function () {
			       this.bindEvent();
			     },
			     bindEvent : function () {
			       var t = this;
			       $('body').on('mousedown','#'+id,function(e){
			         e && e.preventDefault();
			         if ( !t.move) {
			           t.mouseDown(e);
			         }
			       });
			       $('body').on('mouseup','#'+id,function(e){
			         t.mouseUp(e);
			       });
			       $('body').on('mousemove','#'+id,function(e){
			         if (t.down) {
			           t.mouseMove(e);
			         }
			       });
			     },
			     mouseMove : function (e) {
			       e && e.preventDefault();
			       this.move = true;
			       var x = this._x - e.clientX,
			           y = this._y - e.clientY,
			           dom = document.getElementById(id);
			       dom.scrollLeft = (this._left + x);
			       dom.scrollTop = (this._top + y);
			     },
			     mouseUp : function (e) {
			       e && e.preventDefault();
			       this.move = false;
			       this.down = false;
			       this.dragWrap.css('cursor','');
			     },
			     mouseDown : function (e) {
			       this.move = false;
			       this.down = true;
			       this._x = e.clientX;
			       this._y = e.clientY;
			       this._top = document.getElementById(id).scrollTop;
			       this._left = document.getElementById(id).scrollLeft;
			       this.dragWrap.css('cursor','move');
			     }
			   };
			   var drag = new drag();
		}
}
 
 
function processHiddenInput(inputName,hiddenInputId){
	var ids=getCheckedIds(inputName);
	$("#"+hiddenInputId).val(ids);
}
/**
 * checkbox工具类封装
 */
var CheckboxUtil={
		  init:function(parentId){
			  var that=this;
			  var checkboxs=null;
			  if(parentId){
				  checkboxs=$('#'+parentId).find("[data-checkbox]")
			  }else{
				  checkboxs=$("body").find("[data-checkbox]");
			  }
			  if(checkboxs&&checkboxs.length>0){
				  var initCheckBoxEvent=function(ck,name,hiddenInputId,handler){
					  ck.find("input[type='checkbox'][name='"+name+"']").unbind("change").on("change",function(){
							if(handler){
							  var exe=eval(handler);
							  if(exe){
								  if(handler=="processHiddenInput"){
									  processHiddenInput(name,hiddenInputId);
								  }else{
									  var input=$(this);
									  exe(input,input.is(":checked"));
								  }
								  
							  }
						  }
					  });
				  }
				  
				  checkboxs.each(function(){
					var ck=$(this);
					var handler=ck.data("handler");
					var name=ck.data("name");
					var value=ck.data("value");
					var defaultValue=ck.data("default");
					var hiddenInputId=ck.data("hiddeninput");
					 var url=ck.data("url");
					  var label=ck.data("label");
					
					  if(url){
						  that.insertDatas(ck,url,name,label,function(){
							  initCheckBoxEvent(ck,name,hiddenInputId,handler);
							  that.setChecked(name,value,defaultValue);
						  });
					  }else{
						  initCheckBoxEvent(ck,name,hiddenInputId,handler);
						  that.setChecked(name,value,defaultValue);
					  }
					
				 });
			  }
		  },insertDatas:function(ck,url,name,label,callback){
			  var that=this;
			  ck.empty();
			  
			  var width=ck.data("width");
			  var labelWidth="";
			  var radioWidth="";
			  if(width){
				  var arr=width.split(",");
				  labelWidth=arr[0];
				  radioWidth=arr[1];
			  }else{
				  labelWidth="100px";
				  radioWidth="col";
			  }
			  var html='';
			  if(label){
				 if(labelWidth.indexOf("px")!=-1){
					 html= '<label class="col-auto col-form-label" style="width:'+labelWidth+'">'+label+'</label>';
				 }else{
					 html= '<label class="'+labelWidth+' col-form-label">'+label+'</label>';
				 }
			  }
			  
			  var inline="";
			  var isInline=ck.data("inline");
			  if(isInline){
				  inline="checkbox-inline";
			  }
				Ajax.get(url,function(res){
					html+='<div class="'+radioWidth+'"  style="padding-top: 1px;">';
  					var list=res.data;
  					var nodotname=name.replace("\\.","_");
  					if(list&&list.length>0){
  						for(var i in list){
  							nodotname=nodotname+"_"+i;
  							var radioHtml = '<div class="checkbox checkbox-primary '+inline+'">'+
  								'<input  id="'+nodotname+'" type="checkbox" name="'+name+'" value="'+list[i].value+'"/>'+
  									'<label for="'+nodotname+'">'+list[i].text+'</label>'+
  								'</div>';
      						html+=radioHtml;
  	  					}
  						html+="</div>";
  						ck.html(html);
  						
  						if(callback){
  							callback();
  						}
  					}
				});
			  
		  },
		  checkByArray:function(name,values){
			  values=values.toString();
			  if(values.indexOf(",")!=-1){
				  var arr=values.split(",");
				  if(arr&&arr.length>0){
					  for(var i in arr){
						  var input=$("input[type='checkbox'][name='"+name+"'][value='"+arr[i]+"']");
						  input.attr("checked","checked");
					  }
				  }
			  }else{
				  var input=$("input[type='checkbox'][name='"+name+"'][value='"+values+"']");
				  input.attr("checked","checked");
			  }
			  
			  
			  
		  },
		  setChecked:function(name,value,defaultValue){
			  var that=this;
			  if(value){
				  that.checkByArray(name,value);
			  }else{
				  if(defaultValue||defaultValue==0||defaultValue=="0"){
						  that.checkByArray(name,defaultValue);
				  }
			  }
			 
		  }
}

  

 
/**
 * summernote编辑器组件初始化
 */
var SummerNoteEditorUtil={
		ing:false,
		init:function(parentId){
			var that=this;
			that.ing=false;
			var editors=null;
			if(parentId){
				editors=$('#'+parentId).find("[data-editor='summernote']")
			}else{
				editors=$("body").find("[data-editor='summernote']");
			}
			if(editors&&editors.length>0){
				editors.each(function(){
					that.initEditor($(this));
				});
			}
			
		},initEditor:function(snEditor){
			var that=this;
			var width=snEditor.data("width");
			var height=snEditor.data("height");
			var placeholder=snEditor.data("placeholder");
			var options={lang:"zh-CN", toolbar: [
				['font', ['bold', 'italic', 'underline', 'clear','strikethrough', 'superscript', 'subscript']],
			    ['fontsize', ['fontsize']],
			    ['color', ['color']],
			    ['para', ['ul', 'ol', 'paragraph']],
			    ['insert', ['table','link', 'picture']],
			    ['misc',['fullscreen','undo','redo']]
			  ], callbacks: {
		          onImageUpload: function(files, editor, $editable) {
		        	  if(that.ing){
		        		     alert("有文件正在上传，请稍后~~");
		        	  }else{
		        		  that.ing=true;
		        		  var len=files.length;
		            	  for(var i=0;i<len;i++){
		            			if(files[i].size/1024>200){
		        		    		that.ing=false; 
		        		    		alert("图片文件不能大于200k");
		        		    		return false;
		        				}
		            		that.sendFile(snEditor,files[i]);
		                  }
		            	  that.ing=false;
		        	  }
		                  
		             
		          }, onChange: function(contents, $editable) {
		        	  var hiddenInputId=snEditor.data("hiddeninput");
		        	  if(hiddenInputId){
		        		  var hidden=$("#"+hiddenInputId);
		        		  if(hidden&&hidden.length>0){
		        			  hidden.val(contents);
		        		  }
		        	  }
		          },onPaste:function(e){
		        	  if(that.ing){
		        		  alert("有文件正在上传，请稍后~~");
		        	  }else{
		        		  that.parseIamge(e,snEditor);
		        	  }
		        	  
		          }
		      }};
			if(placeholder){
				options.placeholder=placeholder;
			}
			if(width){
				options.width=width+"px";
			}
			if(!height){
				height=300;
			}
			options.height=height+"px";
			snEditor.summernote(options);
			
		},parseIamge:function(e,editor){
			 var that=this;
			 that.ing=true; 
			 var eve=e.originalEvent;
			/* var items=eve.clipboardData.items;
			 if(!items||items.length==0){
				 that.ing=false;
				 return false;
			 }
			for(var i in items){
				var item=items[i];
				console.log(item)
				if(item&&item.kind == "file"&&(item.type.match(/^image/))){
					if(item.type.indexOf("png")!=-1||item.type.indexOf("jpg")!=-1||item.type.indexOf("gif")!=-1){
						if(that.ing==false){
							return false;
						}
						that.changeToBolbDataUpload(editor,item);
					}
				}
			}*/
			 
			 var files=eve.clipboardData.files;
			 if(!files||files.length==0){
				 that.ing=false;
				 return false;
			 }
			 eve.stopPropagation();
			 eve.preventDefault();
			 
			for(var i in files){
				var file=files[i];
				if(that.ing==false||!(file.type.match(/^image/))){
					return false;
				}
				if(file.size/1024>200){
		    		that.ing=false; 
		    		alert("剪贴板中图片文件不能大于200k");
		    		return false;
				}
				that.sendFile(editor,file);
			}
			
		}/*,changeToBolbDataUpload:function(editor,file){
			    var that=this;
			    that.ing=true; 
			    var reader = new FileReader();
			    // 读取文件后将其显示在网页中
			    reader.onloadend = function(){
			    	var dataURI=this.result;
			    	var blob=dataURItoBlob(dataURI);
			    	if(blob.size/1024>200){
			    		that.ing=false; 
			    		alert("剪贴板中图片文件不能大于200k");
			    	}else{
			    		that.sendFile(editor,blob);
			    	}
			    };
			    // 读取文件
			    reader.readAsDataURL( file );
		}*/,sendFile:function(editor,file){
			var that=this;
			that.ing=true;
			var imgUploadUrl=editor.data("imguploadurl");
			var fileInputName=editor.data("fileinputname");
			var imghost=editor.data("imghost");
			  var fd = new FormData();
			    fd.append(fileInputName, file);
			    $.ajax({
			        type:"post",
			        url: imgUploadUrl,
			        data: fd,
			        timeout : 60000, //超时时间设置，单位毫秒
			        cache:false, 
			        async:false, 
			        processData: false,
			        contentType: false,
			        success:function (res) {
			        	
			        	if(res.state=="ok"){
			        		if(res.data){
			        			editor.summernote('insertImage', (imghost?imghost:"")+"/"+res.data);
			        		}
			        		LayerMsgBox.success("上传成功",1000);
			        	}else{
			        		LayerMsgBox.error(res.msg,1000);
			        	}
			        	
			        	that.ing=false;
			        	
			        },
			        error:function (err) {
			        	that.ing=false;
			        	LayerMsgBox.error("网络异常",1000);
			        }
			    });
			
		}
}
/**
 * radio工具类封装
 */
var RadioUtil={
		  init:function(parentId){
			  var that=this;
			  var radios=null;
			  if(parentId){
				  radios=$('#'+parentId).find("[data-radio]")
			  }else{
				  radios=$("body").find("[data-radio]");
			  }
			  if(radios&&radios.length>0){
				  var initRadioEvent=function(r,name,handler){
					  if(handler){
						  var exe=eval(handler);
						  if(exe){
							 r.find("input[type='radio'][name='"+name+"']").unbind("click").on("click",function(e){
								  //e.preventDefault();
								  //e.stopPropagation();
								  var r= $(this);
								  var val=r.val();
								  exe(r,val);
							  });
						  }
						 
					  }
				  }
				  radios.each(function(){
					  var r=$(this);
					  var value=r.data("value")+"";
					  if(!value){value="";}else{value=value+""}
					  var defaultValue=r.data("default")+"";
					  if(!defaultValue){defaultValue="";}else{defaultValue=defaultValue+""}
					  var name=r.data("name");
					  var handler=r.data("handler");
					  var url=r.data("url");
					  var label=r.data("label");
					  if(url){
						  that.insertDatas(r,url,name,label,function(){
							  initRadioEvent(r,name,handler);
							  that.setChecked(name,value,defaultValue);
						  });
					  }else{
						  initRadioEvent(r,name,handler);
						  that.setChecked(name,value,defaultValue);
					  }
				  });
			  }
		  },insertDatas:function(r,url,name,label,callback){
			  var that=this;
			  r.empty();
			  
			  var width=r.data("width");
			  var labelWidth="";
			  var radioWidth="";
			  if(width){
				  var arr=width.split(",");
				  labelWidth=arr[0];
				  radioWidth=arr[1];
			  }else{
				  labelWidth="100px";
				  radioWidth="col";
			  }
			  var html='';
			  if(label){
				 if(labelWidth.indexOf("px")!=-1){
					 html= '<label class="col-auto col-form-label" style="width:'+labelWidth+'">'+label+'</label>';
				 }else{
					 html= '<label class="'+labelWidth+' col-form-label">'+label+'</label>';
				 }
			  }
			  
			  var inline="";
			  var isInline=r.data("inline");
			  if(isInline){
				  inline="radio-inline";
			  }
				Ajax.get(url,function(res){
					html+='<div class="'+radioWidth+'"  style="padding-top: 1px;">';
  					var list=res.data;
  					var nodotname=name.replace("\\.","_");
  					if(list&&list.length>0){
  						for(var i in list){
  							nodotname=nodotname+"_"+i;
  							var radioHtml = '<div class="radio radio-primary '+inline+'">'+
  								'<input  id="'+nodotname+'" type="radio" name="'+name+'" value="'+list[i].value+'"/>'+
  									'<label for="'+nodotname+'">'+list[i].text+'</label>'+
  								'</div>';
      						html+=radioHtml;
  	  					}
  						html+="</div>";
  						r.html(html);
  						
  						if(callback){
  							callback();
  						}
  					}
				});
			  
		  },
		  setChecked:function(name,value,defaultValue){
			
			  if(value&&value.length>0){
				  $("input[type='radio'][name='"+name+"'][value='"+value+"']").click();
			  }else{
				  if(defaultValue){
					  if(defaultValue=="all"){
						 $("input[type='radio'][name='"+name+"'][data-all]").click();
					  }else if(defaultValue=="options_first"){
						  $("input[type='radio'][name='"+name+"']:first").click();
					  }else if(defaultValue=="options_last"){
						  $("input[type='radio'][name='"+name+"']:last").click();
					  }else{
						  $("input[type='radio'][name='"+name+"'][value='"+defaultValue+"']").click();
					  }
					 
				  }
			  }
		  }
}


//弹出tips
var LayerTipsUtil={
		  init:function(parentId){
			  var tips=null;
			  if(parentId){
				  tips=$('#'+parentId).find("[data-tipsbtn]");
			  }else{
				  tips=$("body").find("[data-tipsbtn]");
			  }
			  var hasTips=tips.length>0;
			  if(hasTips){
				  tips.each(function(){
					 var t=$(this);
					 var trigger=t.data("trigger");
					 if(trigger&&trigger=="click"){
						 var tipsIndex=0;
						 t.on("click",function(e){
							 e.stopPropagation();
							 e.preventDefault();
							  var tipsMsg=$(this).data("content");
							  tipsIndex=layer.tips(tipsMsg, this, {
								  tips: [4, '#3595CC'],
								  time: 10000
								});
							  
							  $("#layui-layer"+tipsIndex).on("click",function(e){
									 e.stopPropagation();
									 e.preventDefault();
								 });
								 $("body").on("click",function(){
									  layer.close(tipsIndex);
								  });
								 
						  });
						
						 
					 }else{
						 var tipsIndex=0;
						 t.on("mouseover",function(){
							  var tipsMsg=$(this).data("content");
							  tipsIndex=layer.tips(tipsMsg, this, {
								  tips: [4, '#3595CC'],
								  time: 4000
								});
						  }).on("mouseout",function(){
							  layer.close(tipsIndex);
						  });
					 }
				  });
			  }
		  }
}

/**
 * layerPhoto弹出层组件工具类
 */
var LayerPhotoUtil={
		init:function(parentId){
			  var that=this;
			  var radios=null;
			  if(parentId){
				  photoBtns=$('#'+parentId).find("[data-photobtn]")
			  }else{
				  photoBtns=$("body").find("[data-photobtn]");
			  }
			  if(photoBtns&&photoBtns.length>0){
				  photoBtns.unbind("click").on("click",function(e){
					  e.preventDefault();
					  e.stopPropagation();
					  var btn= $(this);
					  var url=null;
					  if(this.tagName.toLowerCase()=="a"){
						  url=btn.attr("href");
					  }else if(this.tagName.toLowerCase()=="img"){
						url=btn.attr("src");
					}
					if(!url){
						url=btn.data("url");
					}
					if(url){
						 layer.photos({
							    photos: {
							    	  "title": "JBolt图片查看器", 
							    	  "start": 0, //初始显示的图片序号，默认0
							    	  "data": [
							    	    {
							    	      "src":url, //原图地址
							    	    }
							    	  ]
							    	}
							    ,anim: 5 //0-6的选择，指定弹出图片动画类型，默认随机（请注意，3.0之前的版本用shift参数）
							  });
					}else{
						alert("页面存在未设置图片地址的 photobtn");
					}
					 
				  });
			  }
				 
		}
}
/**
 * 自动Ajax加载内容的Portal
 */
;(function($){
		$.extend($.fn, {
			ajaxPortal:function(replaceBody,url,replaceOldUrl,callback){
				return this.each(function(){
					var portal=$(this);
					var l_url="";
					if(url){
						l_url=url;
					}else{
						l_url=portal.data("url")
					}
					if(l_url.indexOf("?")!=-1){
						l_url=l_url+"&t="+new Date().getTime();
					}else{
						l_url=l_url+"?t="+new Date().getTime();
					}
					var autoload=portal.data("autoload");
					if(autoload==undefined){
						autoload=true;
					}
					if((replaceBody==undefined&&autoload)||(replaceBody!=undefined)){
						$.get(l_url,function(html){
							if(replaceBody){
								portal.empty().html(html);
							}else{
								portal.append(html);
							}
							if(replaceOldUrl&&url){
								portal.data("url",url);
							}
							var portalId=portal.attr("id");
							if(!portalId){
								portalId=randomId();
								portal.attr("id",portalId);
							}
							
							afterAjaxPortal(portalId);
							if(callback){
								callback();
							}
						});
					
					}
				});
			}
		});
	})(jQuery);

function afterAjaxPortal(portalId){
	var portal=$("#"+portalId);
	portal.find("[tooltip]").tooltip();
	portal.find('[data-toggle="tooltip"]').tooltip();
	SelectUtil.autoLoad({"parent":portal});
	SelectUtil.initAutoSetValue(portalId);
	JAtomFileUploadUtil.init(portalId);
	JAtomImgUploader.init(portalId);
	SwitchBtn.init(portalId);
	RadioUtil.init(portalId);
	CheckboxUtil.init(portalId);
	LayerPhotoUtil.init(portalId);
	LayerTipsUtil.init(portalId);
}

/**
 * 检测文件大小
 * @param file 
 * @param maxSize kb单位
 * @returns
 */
function validateFileMaxSize(file,maxSize){
	  var fileSize=(file.files[0].size/1024).toFixed(1);
	  var gt=(fileSize>maxSize);
	  var formateSize=fileSize+"KB";
	  if(fileSize>1024){
		  formateSize=((fileSize/1024).toFixed(1))+"M";
	  }
	  if(gt){LayerMsgBox.alert("您选择的文件["+formateSize+"],上传限制不能超过 "+maxSize+"KB",2);}
    return gt;
}

//限制上传文件的类型和大小
function validateExcel(file,maxSize){
	  // 返回 KB，保留小数点后两位
	  var fileName = file.value;
	  if(!/.(xls|xlsx)$/.test(fileName)){
		  LayerMsgBox.alert("文件类型必须是xls,xlsx中的一种",2);
		  return false;
	  }
	  if(validateFileMaxSize(file,maxSize)){
		  return false;
	  }
	  return true;
}
//限制上传文件的类型和大小
function validateNormal(file,maxSize){
	  // 返回 KB，保留小数点后两位
	  var fileName = file.value;
	  if(!/.(xls|xlsx|jpg|jpeg|png|JPG|rar|zip|pdf|mp4|flv|mp3|doc|docx)$/.test(fileName)){
		  LayerMsgBox.alert("此文件类型不允许上传",2);
		  return false;
	  }
	  if(validateFileMaxSize(file,maxSize)){
		  return false;
	  }
	  return true;
}
/**
 * 判断是不是img
 */
function isImg(fileName){
	  return /.(jpg|jpeg|png|JPG)$/.test(fileName);
}
//限制上传文件的类型和大小
function validateImg(file,maxSize){
    // 返回 KB，保留小数点后两位
    var fileName = file.value;
    if(isImg(fileName)==false){
  	  	 LayerMsgBox.alert("图片类型必须是jpeg,jpg,png中的一种",2);
           return false;
     }
    if(validateFileMaxSize(file,maxSize)){
  	  return false;
    }
    return true;
}
//验证file
function validateFile(file,accept,maxSize){
	  	var ele=file[0];
	  	//默认两M
	  	if(!maxSize){maxSize=1024*1024*1024*2;}
	  	var passValidate=true;
	  	if(accept){
			switch (accept) {
			case "img":
				passValidate=validateImg(ele,maxSize);
				break;
			case "excel":
				passValidate=validateExcel(ele,maxSize);
				break;
			case "file":
				passValidate=validateNormal(ele,maxSize);
				break;
			}
		}else{
			passValidate=validateNormal(ele,maxSize);
		}
	  	return passValidate;
}



//建立一個可存取到該file的url
function getObjectURL(ele) {
	  var file=ele.files[0];
	  var url = null ;
	  if (window.createObjectURL!=undefined) { // basic
		  url = window.createObjectURL(file);
	  } else if (window.URL!=undefined) { // mozilla(firefox)
		  url = window.URL.createObjectURL(file);
	  } else if (window.webkitURL!=undefined) { // webkit or chrome
		  url = window.webkitURL.createObjectURL(file) ;
	  }
	  return url ;
}

//专门图片上传组件
var JAtomImgUploader={
		  tpl:'<input type="file"   {@if rule}data-rule="${rule}"{@/if}  {@if tips}data-tips="${tips}"{@/if} ><p class="j_img_uploder_msg"><span class="j_file_name"></span><i class="fa fa-remove j_text_danger j_remove_file"></i></p>',
		  init:function(parentId){
				var that=this;
				var parent=parentId?("#"+parentId):"body";
				var imgBoxs=$(parent).find(".j_img_uploder");
				imgBoxs.each(function(){
					var box=$(this);
					JAtomImgUploader.initSingle(box);
				});
				
		  },
		  initSingle:function(box){
			  var that=this;
			    var rule=box.data("rule");
				var tips=box.data("tips");
				var area=box.data("area");
				if(area){
					var arr=area.split(",");
					box.css({
						"width":arr[0],
						"height":arr[1]
					})
				}
				
				box.html(juicer(that.tpl,{rule:rule,tips:tips}));
				var value=box.data("value");
				if(value&&value!="/assets/img/uploadimg.png"){
					var bg="#999 url("+value+") center center no-repeat";
					box.css({
						"background":bg,
						"background-size":"100%"
					});
					box.find("p.j_img_uploder_msg").show();
				}
				$(box).on("change","input[type='file']",function(event){
					var file=$(this);
//					 var files = event.target.files;  
					that.changeFile(file);
				}).on("click",".j_remove_file",function(){
					var removefile=$(this);
					that.removeFile(removefile);
				});

		  },
		  removeFile:function(removeBtn){
			  var uploder=removeBtn.closest(".j_img_uploder");
			  var removehandler=uploder.data("removehandler");
				uploder.find("input[type='file']").val("");
				uploder.find("span.j_file_name").text("");
				uploder.find("p.j_img_uploder_msg").hide();
				uploder.css({
					"background":"url('assets/img/uploadimg.png') center center no-repeat",
					"background-size":"80%"
				});
				var hiddeninput=uploder.data("hiddeninput");
				if(hiddeninput){
					$("#"+hiddeninput).val("");
				}
				if(removehandler){
					var exe=eval(removehandler);
					if(exe){
						exe(uploder);
					}
				}
		  },
		  changeFile:function(file){
			  var uploder=file.closest(".j_img_uploder");
				var maxSize=uploder.data("maxsize");
				var fileValue=file.val();
				var hiddeninput=uploder.data("hiddeninput");
				var handler=uploder.data("handler");
				if(hiddeninput&&handler&&handler!="uploadMultipleFile"){
					$("#"+hiddeninput).val("");
				}
				
				if(fileValue){
					if(validateFile(file,"img",maxSize)){
						var arr=fileValue.split('\\');
						var fileName=arr[arr.length-1];
						if(handler&&handler!="uploadMultipleFile"){
							uploder.find("span.j_file_name").text(fileName).attr("title",fileName);
							uploder.find("p.j_img_uploder_msg").show();
							
							//出预览图
							var fileData=getObjectURL(file[0]);
							if(fileData){
								uploder.css({
									"background":"#999 url('"+fileData+"') center center no-repeat",
									"background-size":"100%"
								});
							}
						}
						uploder.closest(".form-group").removeClass("has-error");
						
							
					}else{
						file.val("");
					}
				}else{
					uploder.find("input[type='file']").val("");
					uploder.find("span.j_file_name").text("");
					uploder.find("p.j_img_uploder_msg").hide();
					uploder.css({
						"background":"url('assets/img/uploadimg.png') center center no-repeat",
						"background-size":"80%"
					});
				}
			
				if(handler){
					var isMultiple=(handler=="uploadMultipleFile");
					if(handler=="uploadFile"||isMultiple){
						var url=uploder.data("url");
						if(!url){
							LayerMsgBox.alert("未设置文件上传地址 data-url",2);
						}else{
							var hiddeninput=uploder.data("hiddeninput");
							var imgbox=uploder.data("imgbox");
							var limit=uploder.data("limit");
							var imgs=$("#"+imgbox+" img");
							if(imgs&&imgs.length>=limit){
								LayerMsgBox.alert("最多上传["+limit+"]张",5)
								uploder.find("input[type='file']").val("");
								uploder.find("span.j_file_name").text("");
								uploder.find("p.j_img_uploder_msg").hide();
								uploder.css({
									"background":"url('assets/img/uploadimg.png') center center no-repeat",
									"background-size":"80%"
								});
								return false;
							}
							
							var fileName=uploder.data("filename");
							if(!fileName){
								fileName="file";
							}
							
							uploadFile("img",url,fileName,file[0].files[0],hiddeninput,null,null,isMultiple,imgbox,function(){
								if(isMultiple){
									uploder.find("input[type='file']").val("");
								}
							});
						}
					}else{
						var exe=eval(handler);
						if(exe){
							exe(file.val(),file);
						}
					}
					
				}
		  }
}

function processImagesHiddenInputValue(imgboxId,hiddenInputId){
	var imgBox=$("#"+imgboxId);
	var hiddenInput=$("#"+hiddenInputId);
	var value="";
	var imgs=imgBox.find("img");
	var length=imgs.length;
	var lindex=length-1;
	if(length>0){
		imgs.each(function(i,item){
			var img=$(this);
			value=value+img.attr("src");
			if(i<lindex){
				value=value+",";
			}
		});
		
	}
	
	
	hiddenInput.val(value);
}

 function removeUploadImgBoxLi(ele){
	var remove=$(ele);
	var li=remove.closest("li");
	var hiddenInputId=li.data("hiddeninput");
	var imgboxId=li.data("imgbox");
	li.remove();
	
	processImagesHiddenInputValue(imgboxId,hiddenInputId);
	
	
}

function imgGotoLeft(i){
	var fa=$(i);
	var li=fa.closest("li");
	var prev=li.prev();
	if(prev){
		var newLi=li.clone();
		prev.before(newLi);
		var hiddenInputId=li.data("hiddeninput");
		var imgboxId=li.data("imgbox");
		li.remove();
		
		processImagesHiddenInputValue(imgboxId,hiddenInputId);
		
		
	}else{
		layer.msg("已经是第一个",{time:1000});
	}
	
}
function imgGotoRight(i){
	var fa=$(i);
	var li=fa.closest("li");
	var next=li.next();
	if(next){
		var newLi=li.clone();
		next.after(newLi);
		var hiddenInputId=li.data("hiddeninput");
		var imgboxId=li.data("imgbox");
		li.remove();
		
		processImagesHiddenInputValue(imgboxId,hiddenInputId);
	}else{
		layer.msg("已经是最后一个",{time:1000});
	}
}

function uploadFile(type,url,name,file,hiddeninput,filenameInput,sizeinput,isMultiple,imgbox,callback){
	 var fileSize=parseInt((file.size/1024).toFixed(0));
	  var fd = new FormData();
	    fd.append(name, file);
	    $.ajax({
	        type:"post",
	        url: url,
	        data: fd,
	        timeout : 60000, //超时时间设置，单位毫秒
	        cache:false, 
	        async:false, 
	        processData: false,
	        contentType: false,
	        success:function (res) {
	        	if(res.state=="ok"){
	        		if(sizeinput){
	        			$("#"+sizeinput).val(fileSize);
	        		}
	        		if((type=="img")&&hiddeninput&&res.data){
	        			var datas=res.data;
	        			var hinput=$("#"+hiddeninput);
	        			var hvalue=$.trim(hinput.val());
	        			if(isMultiple&&imgbox){
	        				$("#"+imgbox).append("<li data-imgbox='"+imgbox+"' data-hiddeninput='"+hiddeninput+"'><img src='"+datas+"'/><div class='optbox' ><i title='删除' onclick='removeUploadImgBoxLi(this)' class='fa fa-trash'></i><i onclick='imgGotoLeft(this)' class='fa fa-arrow-left' title='左移'></i><i title='右移' onclick='imgGotoRight(this)' class='fa fa-arrow-right'></i></div></li>")
	        				if(!hvalue){
	        					hvalue=datas;
	        				}else{
	        					hvalue=hvalue+","+datas;
	        				}
	        			}else{
	        				hvalue=datas;
	        			}
	        			hinput.val(hvalue);
	        		}else
	        		if((type=="file")&&hiddeninput&&res.data){
	        			$("#"+hiddeninput).val(res.data.fileUrl);
	        			if(filenameInput){
	        				var v=$("#"+filenameInput).val();
	        				if((v&&v.length>2)==false){
	        					$("#"+filenameInput).val(res.data.fileName);
	        				}
	        				
	        			}
	        		}
	        		if(callback){callback();}
	        		LayerMsgBox.success("上传成功",1000);
	        	}else{
	        		LayerMsgBox.error(res.msg,1000);
	        	}
	        },
	        error:function (err) {
	        	LayerMsgBox.error("网络异常",1000);
	        }
	    });
}



function dataURItoBlob(dataURI) {
    var byteString = atob(dataURI.split(',')[1]);
    var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
    var ab = new ArrayBuffer(byteString.length);
    var ia = new Uint8Array(ab);
    for (var i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
    }
    return new Blob([ab], {type: mimeString });
}
/**
 * 上传组件封装
 */
var JAtomFileUploadUtil={
		tpl:'<div class="j_upload_file"><input type="file"  {@if rule}data-rule="${rule}"{@/if} {@if tips}data-tips="${tips}"{@/if}  /></div><p class="j_upload_file_box_msg"><span class="j_file_name"></span><i class="fa fa-remove j_text_danger j_remove_file"></i></p>',
		init:function(parentId){
			var that=this;
			var parent=parentId?("#"+parentId):"body";
			var fileBoxs=$(parent).find(".j_upload_file_box");
			fileBoxs.each(function(){
				var box=$(this);
				var rule=box.data("rule");
				var tips=box.data("tips");
				box.html(juicer(that.tpl,{rule:rule,tips:tips}));
			});
			
			fileBoxs.find("input[type='file']").on("change",function(){
				var file=$(this);
				var box=file.closest(".j_upload_file_box");
				var accept=box.data("accept");
				var maxSize=box.data("maxsize");
				var fileValue=file.val();
				if(fileValue){
					if(validateFile(file,accept,maxSize)){
						var arr=fileValue.split('\\');
						var fileName=arr[arr.length-1];
						box.find("span.j_file_name").text(fileName);
						box.find("p.j_upload_file_box_msg").show();
						box.find(".j_upload_file").addClass("j_reupload");
						box.closest(".form-group").removeClass("has-error");
						var imgpreview=box.data("imgpreview");
						//如果是图片 而且设置了要出预览 就出预览图
						if(imgpreview){
							if(isImg(fileName)){
								var imgpreviewBox=$(imgpreview);
								if(imgpreviewBox&&imgpreviewBox.length){
									var url=getObjectURL(file[0]);
									if(url){
										imgpreviewBox.html("<img src='"+url+"'/>");
									}
								}
								
							}
						}
					}else{
						file.val("");
					}
				}else{
					that.clearIt(box);
				}
				
				var handler=box.data("handler");
				if(handler){
					if(handler=="uploadFile"){
						var url=box.data("url");
						if(!url){
							LayerMsgBox.alert("未设置文件上传地址 data-url",2);
						}else{
							var hiddeninput=box.data("hiddeninput");
							var sizeinput=box.data("sizeinput");
							var fileNameInput=box.data("filenameinput");
							var fileName=box.data("filename");
							if(!fileName){
								fileName="file";
							}
							uploadFile("file",url,fileName,file[0].files[0],hiddeninput,fileNameInput,sizeinput);
						}
					}else{
						var exe=eval(handler);
						if(exe){
							exe(file.val(),file);
						}
					}
					
				}
			});
			fileBoxs.find(".j_remove_file").on("click",function(){
				var removefile=$(this);
				var box=removefile.closest(".j_upload_file_box");
				that.clearIt(box);
			});
			
			
		},clearIt:function(box){
			box.find("input[type='file']").val("");
			box.find("span.j_file_name").text("");
			box.find("p.j_upload_file_box_msg").hide();
			box.find(".j_upload_file").removeClass("j_reupload");
			var imgpreview=box.data("imgpreview");
			if(imgpreview){
				$(imgpreview).empty();
			}
		}
}




//layer msg模块封装
var LayerMsgBox={
		alert:function(msg,icon,handler){
			if(icon){
				layer.alert(msg,{icon:icon}, function(index){
						if(handler){
							handler();
						}
					  layer.close(index);
					});  
			}else{
				layer.alert(msg, function(index){
					if(handler){
						handler();
					}
				  layer.close(index);
				});  
			}
			
		},
		confirm:function(msg,handler,cancelHandler){
			layer.confirm(msg, {icon: 3, title:'提示'}, function(index){
				if(handler){
					handler();
				}
				layer.close(index);
			},function(index){
				if(cancelHandler){
					cancelHandler();
				}
				layer.close(index);
			});
		},
		/**
		 * 弹出成功信息,并执行回调方法
		 * @param msg
		 * @param time
		 * @param handler
		 */
		success:function(msg,time,handler){
			if(!msg){msg="操作成功";}
			if(!time){time=1000;}
			var index=layer.msg(msg,{time:time,icon:1},function(){
				if(handler){
					handler();
				}
			});
			return index;
		},


		/**
		 * 弹出Error,并执行回调方法
		 * @param msg
		 * @param time
		 */
		error:function(msg,time,handler){
			if(!msg){msg="错误";}
			if(!time){time=1500;}
			var index=layer.msg(msg,{time:time,icon:2},function(){
				if(handler){
					handler();
				}
			});
			return index;
		},
		prompt:function(title,defaultMsg,handler,type){
			if(type==undefined){
				type=2;
			}
			var i=layer.prompt({title: title,value:(defaultMsg?defaultMsg:""),formType: type}, function(text, index){
				if(handler){
					handler(index,text);
				}
			});
			return i;
		},
		/**
		 * 弹出进度
		 * @param msg
		 * @param time
		 */
		loading:function(msg,time,handler){
			if(!msg){msg="执行中...";}
			var index=null;
			time=(time?time:10000);
			if(time){
				index=layer.msg(msg,{time:time,icon:16,shade:0.3},function(){
					if(handler){
						handler();
					}
				});
			}else{
				index=layer.msg(msg,{icon:16});
			}
			return index;
		},
		close:function(index){
			layer.close(index);
		},
		closeAll:function(type){
			if(type){
				layer.closeAll(type);
			}else{
				layer.closeAll();
			}
		},
		closeLoading:function(){
			setTimeout(function(){
				layer.closeAll('dialog'); //关闭加载层
			}, 500);
		},
		closeLoadingNow:function(){
				layer.closeAll('dialog'); //关闭加载层
		},
		load:function(type,time){
			if(time){
				layer.load(type,{time:time});
			}else{
				layer.load(type);
			}
		},
		closeLoad:function(){
			setTimeout(function(){
				layer.closeAll('loading'); //关闭加载层
			}, 200);
		},
		closeLoadNow:function(){
				layer.closeAll('loading'); //关闭加载层
		}

}

/**
 * ajax封装
 */
var Ajax={
		  post:function(url,data,success,error,sync){
			    var async=true;
			    if(sync){async=false;}
				$.ajax({
					url:url,
					type:"post",
					dataType:"json",
					timeout : 60000, //超时时间设置，单位毫秒
					async:async,
					data:data,
					success:function(data){
						if(data.state=="ok"){
							if(success){
								success(data);
							}
						}else{
							LayerMsgBox.alert(data.msg,2);
							if(error){
								error();
							}
						}
					},
					error:function(){
						LayerMsgBox.alert("网络通讯异常",2);
						if(error){
							error();
						}
						
					}
					
				})
			},
			get:function(url,success,error,sync){
				var async=true;
			    if(sync){async=false;}
				$.ajax({
					url:url,
					type:"get",
					dataType:"json",
					timeout : 60000, //超时时间设置，单位毫秒
					async:async,
					success:function(data){
						if(data.state=="ok"){
							if(success){
								success(data);
							}
						}else{
							LayerMsgBox.alert(data.msg,2);
							if(error){
								error();
							}
						}
					},
					error:function(){
						LayerMsgBox.alert("网络通讯异常",2);
						if(error){
							error();
						}
					}
					
				})
			}
}


/**
   * select工具类
   */
  var SelectUtil={
		  /**
		   * 处理select
		   * setting selectId parent callback
		   */
		   autoLoad:function(setting){
			   var processItems=function(html,list,appendHandler){
					for(var i in list){
  						var option = '<option value="'+list[i].value+'">&nbsp;&nbsp;&nbsp;&nbsp; ∟'+list[i].text+'</option>';
  						if(appendHandler){
							option=appendHandler(option,list[i]);
						}
  						html+=option;
  					}
					return html;
			   }
			   var insert=function(_this,refreshing){
		      		var selectedValue=_this.data("select");
		      		if(refreshing){
		      			selectedValue=_this.val();
		      		}
		      		var handler=_this.data("handler");
		      		var appendHandler = _this.data("append");
		      		if(appendHandler){
						appendHandler=eval(appendHandler);
  					}
		      		_this.empty();
		      		if(_this.data("text")){
		      			_this.append('<option value="'+_this.data("value")+'">'+_this.data("text")+'</option>');
		      		}
		      		var url=null;
		      		if(setting&&setting.url){
		      			url=setting.url;
		      		}else{
		      			url=_this.data("url");
		      		}
		      		if(url!=null){
		      			$.ajax({
			      			type:"GET",
			      			url:url,
			      			dataType:"json",
			      			timeout : 10000, //超时时间设置，单位毫秒
			      			context:_this,
			      			success:function(result){
			      				if(refreshing){
			      					LayerMsgBox.closeLoading();
			      				}
			      				if(result.state=="ok"){
			      					var html="";
			      					var list=result.data;
			      					for(var i in list){
			      						var option = '<option value="'+list[i].value+'">'+list[i].text+'</option>';
			      						if(appendHandler){
											option=appendHandler(option,list[i]);
										}
			      						html+=option;
			      						var items=list[i].items;
			      						if(items&&items.length>0){
			      							html=processItems(html,items,appendHandler);
			      						}
			      					}
			      					_this.append(html);
			      					if(selectedValue||(typeof(selectedValue)=="boolean")){
			      						selectedValue=selectedValue.toString();
			      						if(selectedValue.indexOf(",")!=-1){
				      						var arr=selectedValue.split(",");
				      						_this.val(arr);
				      					}else{
				      						_this.val(selectedValue);
				      					}
			      						if(!_this.val()){
			      							var options=_this.find("option");
			      							if(options&&options.length>0){
			      								_this.val(options.eq(0).val());
			      							}
			      						}
			      					}
			      					
			      					
			      					/*if(_this.hasClass("linkage")){
			      						_this.change();
			      					}*/
			      					_this.change();
			      				}
			      				if(setting&&setting.handler){
			      					setting.handler(_this);
			      				}
			      				if(handler){
									var exe=eval(handler);
									if(exe){
										exe(_this);
									}
								}
			      			}
			      		});
		      		}
		      		
		      	}
		      	var select=null;
		      	if(setting){
		      		if(setting.selectId){
		      			if(setting.parent){
		      				if(typeof setting.parent=="object"){
		      					select=setting.parent.find("#"+setting.selectId);
		      				}else{
		      					if(setting.parent.indexOf("#")!=-1){
		          					select=$(setting.parent).find("#"+setting.selectId);
		          				}else{
		          					select=$("#"+setting.parent).find("#"+setting.selectId);
		          				}
		      				}
		      				
		      			}else{
		      				select=$("#"+setting.selectId);
		      			}
		      			
		      		}else{
		      			if(setting.parent){
		      				if(typeof setting.parent=="object"){
		      					select=setting.parent.find("select[data-autoload]");
		      				}else{
		  	    				if(setting.parent.indexOf("#")!=-1){
		  	    					select=$(setting.parent).find("select[data-autoload]");
		  	    				}else{
		  	    					select=$("#"+setting.parent).find("select[data-autoload]");
		  	    				}
		      				}
		      			}else{
		      				select=$("select[data-autoload]"); 
		      			}
		      		}
		      	}else{
		      		select=$("select[data-autoload]");
		      	}
		      	select.each(function(i,obj){
		      		var _this=$(this);
		      		var islinkage=_this.data("linkage");
		      		//if(_this.data("toggle")=="autoselect"&&islinkage){
		      			_this.unbind("change");
		      		//}
		      		_this.on("change",function(){
		      			var beforechange=_this.data("beforechange");
		      			if(beforechange){
							var exe=eval(beforechange);
							if(exe){
								exe(_this);
							}
						}
		      			var sonId=_this.data("sonid");
		      			if(islinkage&&sonId){
		      				var srcUrl=$("#"+sonId).data("srcurl");
		      				var url="";
		      				if(srcUrl){
		      					url=srcUrl+"/"+_this.val();
		      				}else{
		      					url=$("#"+sonId).data("url")+"/"+_this.val();
		      				}
		      				SelectUtil.autoLoad({parent:_this.parents("form"),selectId:sonId,url:url});
		      			}
		      		});
		      		if((_this.data("url")||setting.url)){
		  	    		insert(_this);
		  	    		if(_this.data("refresh")){
		  	    			var exist=_this.parent().find(".glyphicon-refresh");
		  	    			if(exist&&exist.size()>0){
		  	    				exist.parent().remove();
		  	    			}
		  	    			
		  	    			var refreshBtn=document.createElement("div");
		  	    			refreshBtn.className="input-group-addon handcursor";
		  	    			refreshBtn.innerHTML='<span class="glyphicon glyphicon-refresh"></span>';
		  	    			$(refreshBtn).click(function(){
		  	    				LayerMsgBox.loading("正在刷新数据...",10000);
		  	    				insert(_this,true);
		  	    			});
		  	    			obj.parentElement.appendChild(refreshBtn);
		  	    			
		  	    		}
		  	    		
		  	    		
		      		}else{
		      			var selectedValue=_this.data("select");
		      			if(selectedValue){
		      				_this.val(selectedValue);
		      			}
		      		}
		      	});
		      },
  /**
   * 设置select选中值
   * @param id
   * @param value
   */
   setValue:function(id,value,defaultValue){
	  if(value){
		  $("#"+id).val(value);
	  }else{
		  if(defaultValue){
			  $("#"+id).val(defaultValue);
		  }
	  }

  },initAutoSetValue:function(parentId){
	  $(parentId?("#"+parentId):"body").find("select[data-autosetvalue]").each(function(){
		  var select=$(this);
		  var value=select.data("autosetvalue");
		  select.val(""+value);
	  });
  }
  }
 

$.fn.size=function(){
	return this.length;
}


/**
 * 删除一行tr
 */
function removeTr(obj){
	$(obj).closest("tr").remove();
}
/**
 * 删除一行tr
 */
function removeByKey(obj){
	var removeKey=$(obj).data("removekey");
	$("[data-removekey='"+removeKey+"']").remove();
}

//上移 
function eleMoveUp(current) { 
  var prev = current.prev();  //获取当前<tr>前一个元素
  if (current.index() > 0) { 
    current.insertBefore(prev); //插入到当前<tr>前一个元素前
  } 
} 
// 下移 
function eleMoveDown(current) { 
  var next = current.next(); //获取当前<tr>后面一个元素
  if (next) { 
    current.insertAfter(next);  //插入到当前<tr>后面一个元素后面
  } 
} 


//PageOpt初始化工具
var PageOptUtil={
		  init:function(){
			  var self=this;
			  self.initDialogOptEvent();
			  self.initAjaxLinkEvent();
			  self.initPortalLink();
		  },
		  //初始化弹出dialog的按钮
		  initDialogOptEvent:function(){
			  $(document).on("click","[data-dialogbtn]",function(e){
				  e.preventDefault();
				  e.stopPropagation();
				  var target=$(this).data("target");
				  if(target=="parent"){
				  	parent.DialogUtil.openBy(this);
				  }else if(target=="outparent"){
				  	parent.parent.DialogUtil.openBy(this);
				  }else{
				  	 DialogUtil.openBy(this);
				  }
				  return false;
			  });
		  },
		  initAjaxLinkEvent:function(){
			  $(document).on("click","[data-ajaxbtn]",function(e){
				  e.preventDefault();
				  e.stopPropagation();
				  var action=$(this);
				  var url=action.attr("href");
				  if(!url){
					  url=action.data("url");
				  }
				  if(!url){
					  alert("请设置URL地址");
					  return false;
				  }
				  var okhandler=action.attr("handler");
				  if(!okhandler){
					  okhandler=action.data("handler");
				  }
				  var dataconfirm=action.data("confirm");
				  var dataloading=action.data("loading");
				  var ajaxFun=function(){
					  LayerMsgBox.loading(dataloading,60000);
					  //开始执行ajax
					  Ajax.get(url,function(data){
						  
						  if(okhandler){
							  LayerMsgBox.closeLoadingNow();
							  if(okhandler=="removeTr"){
								  removeTr(action);
							  }else if(okhandler=="removeByKey"){
								  removeByKey(action);
							  }else if(okhandler=="moveUp"){
								  eleMoveUp(action.closest("tr"));
							  }else if(okhandler=="moveDown"){
								  eleMoveDown(action.closest("tr"));
							  }else  if(okhandler=="refreshPortal"){
								  var portalId=action.data("portal");
								  if(portalId){
									  LayerMsgBox.success("操作成功",500,function(){
										  $("#"+portalId).ajaxPortal(true);
									  });
								  }
								  
							  }else{
								  var exe=eval(okhandler);
									if(exe){
										exe(data);
									}
							  }
							 
						  }else{
							  LayerMsgBox.closeLoading();
						  }
					  });
				  }
				  if(dataconfirm){
					  LayerMsgBox.confirm(dataconfirm, ajaxFun);
				  }else{
					  ajaxFun();
				  }
				 
				  return false;
			  });
		  },
		  initPortalLink:function(){
			  $(document).on("click","[data-portalbtn]",function(e){
				  e.preventDefault();
				  e.stopPropagation();
				  var action=$(this);
				  var portalId=action.data("portalid");
				  if(!portalId){
					  alert("请设置data-portalid");
					  return false;
				  }
				  var url=action.attr("href");
				  if(!url){
					  url=action.data("url");
				  }
				  if(!url){
					  alert("请设置URL地址");
					  return false;
				  }
				  var portal=$("#"+portalId);
				  if(!portal||portal.length==0){
					  alert("data-portalid不正确");
					  return false;
				  }
				  LayerMsgBox.loading("正在匹配...",30000);
				  portal.ajaxPortal(true,url,true,function(){
					  LayerMsgBox.success("匹配完成",500);
				  });
				  
			  });
			  
		  }
}

//table初始化工具
var TableUtil={
		  init:function(){
			  var self=this;
			  self.initDelOptEvent();
			  self.initEditOptEvent();
			  self.initAddOptEvent();
		  },
		  //初始化删除按钮
		  initDelOptEvent:function(){
			  $(document).on("click",'.jbolt_table_delbtn',function(e){
				  e.preventDefault();
				  e.stopPropagation();
				  var action=$(this);
				  var url=action.attr("href");
				  var okhandler=action.attr("handler");
				  if(!okhandler){
					  okhandler=action.data("handler");
				  }
				  var confirm=action.data("confirm");
				  LayerMsgBox.confirm(confirm?confirm:"确定删除此项？", function(){
					  LayerMsgBox.loading("删除中",10000);
					  //开始执行ajax
					  Ajax.get(url,function(ret){
						  LayerMsgBox.closeLoading();
						  LayerMsgBox.success(ret.msg?ret.msg:"操作成功");
						  if(okhandler){
							  if(okhandler=="removeTr"){
								  removeTr(action);
							  }else if(okhandler=="removeByKey"){
								  removeByKey(action);
							  }else if(okhandler=="refreshPortal"){
								  var portalId=action.data("portal");
								  if(portalId){
									  LayerMsgBox.success("操作成功",500,function(){
										  $("#"+portalId).ajaxPortal(true);
									  });
								  }
							  }else{
								  var exe=eval(okhandler);
									if(exe){
										exe();
									}
							  }
							 
						  }
					  });
				  });
				  return false;
			  });
		  },
		//初始化删除按钮
		  initEditOptEvent:function(){
			  $(document).on("click",'.jbolt_table_editbtn',function(e){
				  e.preventDefault();
				  e.stopPropagation();
				  var target=$(this).data("target");
				  if(target=="parent"){
				  	parent.DialogUtil.openBy(this);
				  }else if(target=="outparent"){
				  	parent.parent.DialogUtil.openBy(this);
				  }else{
				  	 DialogUtil.openBy(this);
				  }
				  return false;
			  });
		  },
		//初始化新增按钮
		  initAddOptEvent:function(){
			  $(document).on("click",'.jbolt_table_addbtn',function(e){
				  e.preventDefault();
				  e.stopPropagation();
				  var target=$(this).data("target");
				  if(target=="parent"){
				  	parent.DialogUtil.openBy(this);
				  }else if(target=="outparent"){
				  	parent.parent.DialogUtil.openBy(this);
				  }else{
				  	 DialogUtil.openBy(this);
				  }
				  return false;
			  });
		  }
}


//表单中的时间选择组件
var FormDate={
		  init:function(parentId){
			  var dates=null;
			  if(parentId){
				  dates=$('#'+parentId).find("[data-date]")
			  }else{
				  dates=$("body").find("[data-date]");
			  }
			  var hasDate=(dates&&dates.length>0);
			  if(hasDate){
				  dates.attr("readonly","readonly");
				  dates.each(function(){
					  var date=$(this);
					  var dateType=date.data("type");
					  if(!dateType){
						  dateType="date";
					  }
					  var datefmt=date.data("fmt");
					  if(!datefmt){
						  switch (dateType) {
						  case "date":
							  datefmt="yyyy-MM-dd";
							  break;
						  	case "time":
							  datefmt="HH:mm";
							  break;
							case "datetime":
								datefmt="yyyy-MM-dd HH:mm";
								break;
							default:
								datefmt="yyyy-MM-dd";
								break;
							}
					  }
					  var id=date.attr("id");
					  if(!id){
						  id=date.attr("name");
						  id=id.replace(".","");
						  date.attr("id",id);
					  }
					 laydate.render({
						 elem:"#"+id,
						 type:dateType, //日期格式
						 format:datefmt
					 }); 
				  });
			  }
		  }
}



//弹出dialog类库
var DialogUtil={
		  openBy:function(ele){
			  var action=$(ele);
			  var url=action.data("url");
			  if(!url){
				  url=action.attr("href");
			  }
			  if(!url){LayerMsgBox.alert("没有设置dialog的url. href 或者 dialog-url", 2); return false;}
			  var title=action.data("title");
			  var handler=action.attr("handler");
			  if(!handler){
				  handler=action.data("handler");
			  }
			  var dialog_area=action.data("area");
			  var w="800px";
			  var h="500px";
			  if(dialog_area){
				  var area=dialog_area.split(",");
				  var ww=area[0];
				  var hh=area[1];
				  if(ww.indexOf("px")!=-1||ww.indexOf("%")!=-1){
					  w=ww;
				  }else{
					  w=ww+"px";
				  }
				  if(hh.indexOf("px")!=-1||hh.indexOf("%")!=-1){
					  h=hh;
				  }else{
					  h=hh+"px";
				  }
			  }
			  var dialog_scroll=action.data("scroll");
			  if(!dialog_scroll){
				  dialog_scroll="no";
			  }else{
				  dialog_scroll="yes";
			  }
			  var fs=action.data("fs");
			  if(fs&&(fs=="true"||fs==true)){
				  dialog_scroll="yes";
			  }
			  var cdrfp=action.data("cdrfp");
			  if(cdrfp==undefined){
				  cdrfp=false;
			  }
			  var portalId=action.data("portal");
		      var btn=action.data("btn");
		      this.openNewDialog({
		    	  title:title,
		    	  width:w,
		    	  height:h,
		    	  url:url,
		    	  scroll:dialog_scroll,
		    	  btn:btn,
		    	  handler:handler,
		    	  cdrfp:cdrfp,
		    	  fs:fs,
		    	  portalId:portalId
		      });
		  },openNewDialog:function(options){
			  var btn=[];
			  var dbtn=options.btn;
			  if(!dbtn){
		    	  btn=["确定", '关闭'];
		      }else if(dbtn&&dbtn=="no"){
		    	  btn=[];
		      }else if(dbtn&&dbtn=="close"){
		    	  btn=["确定", '关闭'];
		      }
			  var lindex=layer.open({
				  type: 2,
				  title: options.title,
				  shadeClose: false,
				  shade: 0.5,
				  maxmin:true,
				  area: [options.width, options.height],
				  content:[options.url,options.scroll],
				  btn:btn, 
				  yes:function(index){
					  var iframeWin = window[$(".layui-layer-iframe").find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();
					  iframeWin.submitThisForm(function(){
						  LayerMsgBox.close(index);
						  if(options.handler){
							  if(options.handler=="refreshPortal"){
								  if(options.portalId){
									  LayerMsgBox.success("操作成功",500,function(){
										  $("#"+options.portalId).ajaxPortal(true);
									  });
								  }else{
									  LayerMsgBox.alert("没有配置data-portalid",2);
								  }
								 
							  }else{
								  var exe=eval(options.handler);
									if(exe){
										exe();
									}
							  }
							  
						  }
					  });
				  },end:function(){
					  if(options.cdrfp){
						  refreshPjaxContainer();
					  }
				  }
			  });
			  if(options.fs){
				  layer.full(lindex);
			  }
			  if(dbtn&&dbtn=="close"){
				  $("#layui-layer"+lindex).find("a.layui-layer-btn0").hide();
			  }
			  if(dbtn&&dbtn=="no"){
				  $("#layui-layer"+lindex).find(".layui-layer-btn").remove();
			  }
		  }
}


/********************************************************************
 ************************表单验证 开始******************************
 ********************************************************************/  
  String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g, "");}
Date.prototype.format = function (fmt) { 
    var o = {
        "M+": this.getMonth() + 1, 
        "d+": this.getDate(), 
        "h+": this.getHours(), 
        "m+": this.getMinutes(),
        "s+": this.getSeconds(), 
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S": this.getMilliseconds()  
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
  /**
   * 判断对象是否为array
   * @param obj
   * @returns {Boolean}
   */
  function isArray(obj){ 
  	return (typeof obj=='object')&&obj.constructor==Array; 
  } 
  
  function isEmpty(something){
  	return (something=="undefined"||something==null||something==""||something==false);
  	}

  function isNotEmpty(something){
  	return ((something!=null&&something!="undefined"&&something!="")||something==true);
  	}


  var g = function (id) {
      return "string" == typeof id ? document.getElementById(id) : id;
  };
//摇摆摇摆摇摆起来
  jQuery.fn.shake = function(intShakes /*Amount of shakes*/, intDistance /*Shake distance*/, intDuration /*Time duration*/) {
      this.each(function() {
          var jqNode = $(this);
          jqNode.css({position: 'relative'});
          for (var x=1; x<=intShakes; x++) {
              jqNode.animate({ left: (intDistance * -1) },(((intDuration / intShakes) / 4)))
              .animate({ left: intDistance },((intDuration/intShakes)/2))
              .animate({ left: 0 },(((intDuration/intShakes)/4)));
          }
      });
      return this;
  }
  function processCheckTab(input){
  	var tabpanel=input.closest("div[role='tabpanel']");
  	if(tabpanel&&tabpanel.size()==1){
  		var id=tabpanel.attr("id");
  		$(".nav.nav-tabs .nav-item.nav-link[href='#"+id+"']").tab("show");
  	}
  }
  
  function showItCheckResult(input){
  	input.parents(".form-group").removeClass("bdc-success").addClass("bdc-danger");
          input.shake(2,10,400);
          input.focus();
  }
//调用正则表达式验证
  function TestRgexp(re, s) {
      return re.test(s);
  }
//验证规则map
  var ruleMap=[
      {type:"number",method:function(value){return (!isNaN(value));}},//数字校验
      {type:"pznumber",method:function(value){return (!isNaN(value)&&value*1>=0);}},//验证正数和0
      {type:"pzint",method:function(value){return ((TestRgexp(/^-?[0-9]\d*$/, value))&&(value*1>=0));}},//验证正数和0
      {type:"pnumber",method:function(value){return (!isNaN(value)&&value*1>0);}},//验证正数
      {type:"int",method:function(value){return TestRgexp(/^-?[0-9]\d*$/, value);}},//整数校验
      {type:"pint",method:function(value){return TestRgexp(/^[0-9]*[1-9][0-9]*$/, value);}},//正整数校验
      {type:"email",method:function(value){return TestRgexp(/\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/, value);}},//Email校验
      {type:"filepath",method:function(value){return TestRgexp(/^([a-zA-Z]){1}:(\\[^\/\\:\*\?\"<>]+)*(\\)?$/, value);}},//URL校验
      {type:"url",method:function(value){return TestRgexp(/^([a-zA-Z]){1}:(\\[^\/\\:\*\?\"<>]+)*(\\)?$/, value);}},//URL校验
      {type:"date",method:function(value){return TestRgexp(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/,value);}},//日期验证
      {type:"money",method:function(value){return TestRgexp(/^(?!0\.00)(?:0|[1-9]\d*)(?:\.\d{1,2})?$/,value);}},//金额
      {type:"phone",method:function(value){return TestRgexp(/^(\(\d{3,4}\)|\d{3,4}-|\s)?\d{7,14}$/,value);}},//金额
      {type:"password",method:function(value){
    	  if(!value){return false;}
    	  var len=value.length;
    	  return len>=6&&len<=16;
      }},//密码验证
      {type:"select",method:function(value){
    	  return (value&&value!=""&&value!="0"&&value!="-1"&&value.length>0);
      }}//select
  ];
  var isDOM = ( typeof HTMLElement === 'object' ) ?
          function(obj){
              return obj instanceof HTMLElement;
          } :
          function(obj){
              return obj && typeof obj === 'object' && obj.nodeType === 1 && typeof obj.nodeName === 'string';
          }
  /**
   * 表单验证器
   */
  var FormChecker={
  //检查form表单中的input textarea select
		   checkIt:function(checkObj){
			   var self=this;
			   var input=null;
				  if(typeof checkObj =="string"){
					  input=$("#"+checkObj);
				  }else if(typeof checkObj=="object"){
					  if(isDOM(checkObj)){
						  input=$(checkObj); 
					  }else{
						  input=checkObj;
					  }
				  }
			   var error=true;
		          if(input.is(":disabled")){
		        	  return true;
		          }
		          if(typeof(input.data("rule"))!="undefined"){
		              var flag = self.checktype(input);
		              var next=input.next().hasClass("input-group-addon");
		              var prev=input.prev().hasClass("input-group-addon");
		              var ta=input.is("textarea");
		              if (!flag) {
		              	error=false;
		              	
		              	if(input.is("input")&&input.attr("type")=="hidden"){return;}
		              	input.parents(".form-group").removeClass("bdc-success").addClass("bdc-danger");
		              	
		              	processCheckTab(input);
		                  input.shake(2,10,400);
		                  input.focus();
		                  if(input.is("input")&&input.attr("type")=="file"){return;}
		                  
		              }else{
		              	error=true;
		              	if(input.is("input")&&input.attr("type")=="hidden"){return;}
		              	
		              	input.parents(".form-group").removeClass("bdc-danger").addClass("bdc-success");
		              	if(input.is("input")&&input.attr("type")=="file"){return;}
		              }
		          }
		          return error;
		   },
  check:function(form){
	  var checkForm=null;
	  if(typeof form =="string"){
		  checkForm=$("#"+form);
	  }else if(typeof form=="object"){
		  if(isDOM(form)){
			  checkForm=$(form); 
		  }else{
			  checkForm=form;
		  }
	  }
	  var self=this;
      var error=true;
      checkForm.find("input,textarea,select,[data-rule='checkbox'],[data-rule='radio']").each(function(){
          if(!error){
              return error;
          }
          var input=$(this);
          if(input.is(":disabled")){
        	  return true;
          }
          if(typeof(input.data("rule"))!="undefined"){
              var flag = self.checktype(input);
              var next=input.next().hasClass("input-group-addon");
              var prev=input.prev().hasClass("input-group-addon");
              var ta=input.is("textarea");
              if (!flag) {
              	error=false;
              	
              	if(input.is("input")&&input.attr("type")=="hidden"){return;}
              	input.parents(".form-group").removeClass("bdc-success").addClass("bdc-danger");
              	
              	processCheckTab(input);
                  input.shake(2,10,400);
                  input.focus();
                  if(input.is("input")&&input.attr("type")=="file"){return;}
                  
              }else{
              	error=true;
              	if(input.is("input")&&input.attr("type")=="hidden"){return;}
              	
              	input.parents(".form-group").removeClass("bdc-danger").addClass("bdc-success");
              	if(input.is("input")&&input.attr("type")=="file"){return;}
              }
          }
      });
     
      return error;
  },
  checkCheckboxRequired:function(input,mytips,show){
	  var self=this;
	  var name=input.data("name");
	 	 if(!name){
	 		 return false;
	 	 }
	 	var checked= $("input[type='checkbox'][name='"+name+"']:checked");
	 	if(checked&&checked.length>0){
	 		input.removeClass("bdc-danger").addClass("bdc-success");
	 		return true;
	 	}  
	 	input.removeClass("bdc-success").addClass("bdc-danger");
	 	input.shake(2,10,400);
	 	input.focus();
	 	var tips=input.data("tips");
	 	if(!tips){
	 		tips="必须选择至少一个选项";
	 	}
	 	self.showMyTipsIfNeed(input,tips,show);
	 	return false;
  },
  checkRadioRequired:function(input,mytips,show){
	  var self=this;
	  var name=input.data("name");
	 	 if(!name){
	 		 return false;
	 	 }
	 	var checked= $("input[type='radio'][name='"+name+"']:checked");
	 	if(checked&&checked.length>0){
	 		input.removeClass("bdc-danger").addClass("bdc-success");
	 		return true;
	 	}  
	 	input.removeClass("bdc-success").addClass("bdc-danger");
	 	input.shake(2,10,400);
	 	input.focus();
	 	var tips=input.data("tips");
	 	if(!tips){
	 		tips="必须选择至少一个选项";
	 	}
	 	self.showMyTipsIfNeed(input,tips,show);
	 	return false;
  },
  //检查给定一个input textarea select的值
  checktype:function(input) {
	  var self=this;
      var type=input.data("rule");
      if(!type){return true;}
      var mytips=input.data("tips"),
        show=input.data("show"),
        notNull=input.data("notnull");
      var value=null;
     if(type=="checkbox"){
    	if(notNull==null||notNull==true||notNull=="true"){
    		return self.checkCheckboxRequired(input,mytips,show);
    	}else{
    		return true;
    	}
    		
     }else  if(type=="radio"){
    	if(notNull==null||notNull==true||notNull=="true"){
    		return self.checkRadioRequired(input,mytips,show);
    	}else{
    		return true;
    	}
    		
     }else{
    	 value=input.val();
     }
      
      if (show != null) {
          g(show).innerHTML = "";
      }
      //检测是否为空
  	if(typeof value=="string"){
  		value=value.trim();
  	}
          if (!value) {
              //检查配置是否需要进行非空检测
              if(notNull==null||notNull==true||notNull=="true"){
                      if(!mytips){
                    	  if(input.attr("type")=="file"){
                    		  mytips="此文件必须上传";
                    	  }else{
                    		  mytips="必填项";
                    	  }
                      }
                          if (show == null) {
                        	  LayerMsgBox.error("<span class='j_text_danger'>"+mytips+"</span>",1500);
                        	 /* var pos=input.data("pos");
                        	  if(!pos){pos=2;}
                          	layer.tips(mytips,input,{
                          		tips: [pos, '#d9534f'],
                          		time:4000
                          	});*/
                          } else {
                              g(show).innerHTML = mytips;
                          }
                     
              return false;
              }else{
              return true;
              }
           }
      return self.checkTypes(input,type,value,mytips,show,notNull);

  },
  //检测数据类型
  checkTypes:function(input,type,value,mytips,show,notNull){
	  var self=this;
      //验证多个的时候
      var types=type.split(";");
      var checkFlag=true;
      var error=1;
      var success=2;
      var canNotCheck=3;
      for(var i=0;i<types.length;i++){
          var type=types[i];
          var checkResult=self.checkSelf(input,type,value,mytips,show,notNull);
          if(checkResult!=canNotCheck){//判断能否处理 如果处理了 成功了继续下一个type 失败了则直接整个结束
              if(checkResult==success){
                  continue;
              }else{
                  checkFlag=false;
                  break;
              }
          }
          
          //如果上面不能处理 则进入比较处理
          checkResult=self.checkCompare(input,type,value,mytips,show,notNull);
          if(checkResult!=canNotCheck){//判断能否处理 如果处理了 成功了继续下一个type 失败了则直接整个结束
              if(checkResult==success){
                  continue;
              }else{
                  checkFlag=false;
                  break;
              }
          }
          
          
      }
      return checkFlag;
  },

  //检测比较
   checkCompare:function(input,type,value,mytips,show,notNull){
	   var self=this;
      var error=1;
      var success=2;
      var canNotCheck=3;
      var selfValue=self.getRealTypeValue(value);
      var compareValue=self.getCompareValue(type);
      if(type.indexOf("len=")!=-1){
          if(value.length==compareValue){
              return success;
          }
          if(!mytips){
        	  mytips="长度必须等于"+compareValue;
          }
          self.showMyTipsIfNeed(input,mytips,show);
          return error;
      }else if(type.indexOf("len>=")!=-1){
          if(value.length>=compareValue){
              return success;
          }
          if(!mytips){
        	  mytips="长度必须大于等于"+compareValue;
          }
          self.showMyTipsIfNeed(input,mytips,show);
          return error;
      }else if(type.indexOf("len>")!=-1){
          if(value.length>compareValue){
              return success;
          }
          if(!mytips){
        	  mytips="长度必须大于"+compareValue;
          }
          self.showMyTipsIfNeed(input,mytips,show);
          return error;
      }else if(type.indexOf(">=")!=-1){
      	if(selfValue>=compareValue){
      		return success;
      	}
        if(!mytips){
      	  mytips="必须大于等于"+compareValue;
        }
      	self.showMyTipsIfNeed(input,mytips,show);
      	return error;
      }else if(type.indexOf("len<=")!=-1){
          if(value.length<=compareValue){
              return success;
          }
          if(!mytips){
        	  mytips="长度必须小于等于"+compareValue;
          }
          self.showMyTipsIfNeed(input,mytips,show);
          return error;
      }else if(type.indexOf("len<")!=-1){
          if(value.length<compareValue){
              return success;
          }
          if(!mytips){
        	  mytips="长度必须小于"+compareValue;
          }
          self.showMyTipsIfNeed(input,mytips,show);
          return error;
      }else if(type.indexOf("<=")!=-1){
      	if(selfValue<=compareValue){
      		return success;
      	}
        if(!mytips){
      	  mytips="必须小于等于"+compareValue;
        }
      	self.showMyTipsIfNeed(input,mytips,show);
      	return error;
      }else if(type.indexOf(">")!=-1){
          if(selfValue>compareValue){
              return success;
          }
          if(!mytips){
        	  mytips="必须大于"+compareValue;
          }
          self.showMyTipsIfNeed(input,mytips,show);
          return error;
      }else if(type.indexOf("<")!=-1){
          if(selfValue<compareValue){
              return success;
          }
          if(!mytips){
        	  mytips="必须小于"+compareValue;
          }
          self.showMyTipsIfNeed(input,mytips,show);
          return error;
      }else if(type.indexOf("!=")!=-1){
          if(selfValue!=compareValue){
              return success;
          }
          if(!mytips){
        	  mytips="不能等于"+compareValue;
          }
          self.showMyTipsIfNeed(input,mytips,show);
          return error;
      }else if(type.indexOf("==")!=-1){
          if(selfValue==compareValue){
              return success;
          }
          if(!mytips){
        	  mytips="必须等于"+compareValue;
          }
          self.showMyTipsIfNeed(input,mytips,show);
          return error;
      }

      return canNotCheck;
  },
  //得到正确类型的值
  getRealTypeValue:function(value){
      if(!isNaN(value)){
          return Number(value);
      }
      return value;
  },
  //得到需要比较的值
  getCompareValue:function(type){
	  var self=this;
      if(type.indexOf("#")!=-1){
          var cid=type.substring(type.indexOf("#"));
          return self.getRealTypeValue($(cid).val());
      }else{
    	if(type.indexOf("len=")!=-1){
    		  return self.getRealTypeValue(type.substring(4));
        }
      	if(type.indexOf("len")!=-1){
      		type=type.substring(3);
      	}
      	
          if(type.indexOf("=")!=-1){
              return self.getRealTypeValue(type.substring(2));
          }else{
              return self.getRealTypeValue(type.substring(1));
          }
      }
  },
  //显示提示信息
   showMyTipsIfNeed:function(input,mytips,show){
      if(mytips!=null){
                  if (show == null) {
                      LayerMsgBox.error("<span class='j_text_danger'>"+mytips+"</span>",3000);
                	  /*var pos=input.data("pos");
                	  if(!pos){pos=2;}
                	  layer.tips(mytips,input,{
                		  tips: [pos, '#d9534f'],
                    	  time:4000
                	  });*/
                  } else {
                      g(show).innerHTML = mytips;
                  }
       }
  },
  

  //检测自身
   checkSelf:function(input,type,value,mytips,show,notNull){
	   var self=this;
      var error=1;
      var success=2;
      var canNotCheck=3;
      if(type=="string"){
          return success;
      }
      var process=false;
      for(var i=0;i<ruleMap.length;i++){
          if(type==ruleMap[i].type){
              process=true;
              var result=ruleMap[i].method.call(this,value);
              if (!result) {
            	  if(!mytips){
            		  switch (type) {
            		  case "email":
            			  mytips="Email格式不正确";
            			  break;
            		  case "phone":
            			  mytips="电话号码格式不正确";
            			  break;
            		  case "number":
            			  mytips="必须为数字";
            			  break;
            		  case "pnumber":
            			  mytips="必须为正数";
            			  break;
            		  case "pznumber":
            			  mytips="不能为负数";
            			  break;
            		  case "pzint":
            			  mytips="必须为0或正整数";
            			  break;
            		  case "int":
            			  mytips="必须为整数";
            			  break;
            		  case "pint":
            			  mytips="必须为正整数";
            			  break;
            		  case "date":
            			  mytips="必须为日期格式";
            			  break;
						case "password":
							mytips="密码长度必须为6~16个字符";
							break;
						case "select":
							mytips="必须选择一项";
							break;
					}
            	  }
            	  self.showMyTipsIfNeed(input,mytips,show);
                  return error;
              }
          }
      }
      return process?success:canNotCheck;
   
      
  }
  
  
  }
  /********************************************************************
   ************************表单验证 结束******************************
   ********************************************************************/  
 

/**
 * 初始化左侧导航菜单
 */
function initAdminLeftNav(){
	$("body").on("click",".jbolt_admin_left_navs .jbolt_admin_nav .jbolt_menu_group",function(){
		$(".jbolt_admin").removeClass("hideMenu").addClass("normalMenu");
		localStorage.setItem('jbolt_hideMenu', false);
		var jbolt_admin_left_navs=$(".jbolt_admin_left_navs");
		
		var h1=$(this);
		var nav=h1.closest("nav");
		var expansion=$(".jbolt_admin_left_navs .jbolt_admin_nav.expansion");
		if(!h1.hasClass("l1link")){
			if(jbolt_admin_left_navs.hasClass("allexpansion")){return false;}
			var pkey=expansion.data("key");
			var hpkey=nav.data("key");
			
				var fa=h1.find("i.fa").last();
				if(fa.hasClass("fa-angle-down")){
					fa.removeClass("fa-angle-down").addClass("fa-angle-left");
				}else{
					fa.removeClass("fa-angle-left").addClass("fa-angle-down");
				}
				if(pkey!=hpkey){
					expansion.removeClass("expansion");
				}
				$(this).parent().toggleClass("expansion");
			
		}else{
			expansion.removeClass("expansion");
			expansion.find(".jbolt_menu_group i.fa.fa-angle-down").removeClass("fa-angle-down").addClass("fa-angle-left");
			nav.addClass("expansion");
			nav.find(".jbolt_menu_group i.fa.fa-angle-left").removeClass("fa-angle-left").addClass("fa-angle-down");
		}
		
	});
	
/*	var value=localStorage.getItem('allexpansion');
	if(value=="true"){
		var jbolt_admin=$(".jbolt_admin_left_navs");
		jbolt_admin.addClass("allexpansion");
		jbolt_admin.find(".jbolt_menu_group i.fa.fa-angle-left").removeClass("fa-angle-left").addClass("fa-angle-down");
	}*/
	
	var value=localStorage.getItem('jbolt_hideMenu');
	if(value=="true"){
		$(".jbolt_admin_nav.expansion").removeClass("expansion");
		$(".jbolt_admin").addClass("hideMenu");
	}else{
		$(".jbolt_admin").addClass("normalMenu");
	}
	resizeMorris();
}

function initTabs(){
	$('#myTab a').on("click",function (e) {
	  e.preventDefault();
	  $(this).tab('show');
	});
}


function sendPjax(url, container, extData) {
	$.pjax({
		url: url
		, container: container
		, extData: extData
	});
}

function pjaxSubmitForm(formEle){
	var form=$(formEle);
	var url=formEle.action;
	var datas=form.serialize();
	if(url.indexOf("?")!=-1){
		url=url+"&"+datas;
	}else{
		url=url+"?"+datas;
	}
	$.pjax({
		url: url
		, container: mainPjaxContainer
	});
	return false;
}

function initAdminPjax(){
	$.pjax.defaults.timeout=5000;
	$(document).pjax('a[data-pjax]:not([target]),[data-pjax] a:not([target])', mainPjaxContainer);
	$(document).on('pjax:timeout', function(event) {
		LayerMsgBox.closeLoad();
  		event.preventDefault();
	});
	  //支持表单提交事件无刷新
	   $(document).on('submit', 'form[data-pjaxsubmit]', function (event) {
		   var needcheck=$(this).data("needcheck");
		   if(needcheck){
			   if(FormChecker.check(this)){
				   $.pjax.submit(event, mainPjaxContainer);
			   }
		   }else{
			   $.pjax.submit(event, mainPjaxContainer);
		   }
		   return false;
	    });
	$(document).on('pjax:start', function() {
		LayerMsgBox.load(3);
	});
	$(document).on('pjax:end', function() {
		afterPjax();
		LayerMsgBox.closeLoad();
	});
}

function refreshPjaxContainer(){
	$.pjax.reload(mainPjaxContainer);
}
/**
 * 检测打开的pjax页面 打开打开指定的nav
 * @returns
 */
function initOpenLeftNav(){
	var jbolt_page=$("div.jbolt_page[data-key]");
	if(jbolt_page&&jbolt_page.size()>0){
		var key=jbolt_page.data("key");
		openLeftNav(key);
	}
	
}



/**
 * 打开指定key的左侧nav
 * @param key
 * @returns
 */
function openLeftNav(key){
	var expansion=$(".jbolt_admin_nav.expansion");
	expansion.removeClass("expansion");
	expansion.find(".jbolt_menu_group i.fa.fa-angle-down").removeClass("fa-angle-down").addClass("fa-angle-left");
	
	$(".jbolt_admin_left_navs nav.jbolt_admin_nav a.active").removeClass("active");
	$(".jbolt_admin_left_navs nav.jbolt_admin_nav a").each(function(){
		var item=$(this);
		var href=item.attr("href");
		if(key==href){
			item.addClass("active");
			var nav=item.closest("nav");
			if(nav&&nav.size()==1){
				nav.addClass("expansion");
				nav.find(".jbolt_menu_group i.fa.fa-angle-left").removeClass("fa-angle-left").addClass("fa-angle-down");
			}
			return false;
		}
		
	});
	
	
	
}
function afterPjax(){
	//加载界面后需要选中打开左侧选项卡
	initOpenLeftNav();
	$('.tooltip.show').remove();
	$(mainPjaxContainer+" [tooltip]").tooltip();
	$(mainPjaxContainer+' [data-toggle="tooltip"]').tooltip();
	$("[data-ajaxportal]").ajaxPortal(true);
	SelectUtil.autoLoad({"parent":mainPjaxContainer});
	SelectUtil.initAutoSetValue(mainPjaxContainerId);
	JAtomFileUploadUtil.init(mainPjaxContainerId);
	JAtomImgUploader.init(mainPjaxContainerId);
	SwitchBtn.init(mainPjaxContainerId);
	RadioUtil.init(mainPjaxContainerId);
	FormDate.init(mainPjaxContainerId);
	SummerNoteEditorUtil.init(mainPjaxContainerId);
	CheckboxUtil.init(mainPjaxContainerId);
	LayerPhotoUtil.init(mainPjaxContainerId);
	LayerTipsUtil.init(mainPjaxContainerId);
	getNoticeCount();
}

function getCheckedIds(name){
	var ids=new Array();
	var inputs=$("input[name='"+name+"']:checked");
	inputs.each(function(i){
			ids.push($(this).val());
	});
	if(ids.length>0){
		return ids.join(",");
	}
	
	return "";
}

/**
 * 分页提交form
 */
function pageSubmitForm(id,formId,baseUrl,page){
	  if(!page){
		  var input=$("#gonu");
		  if(input&&input.length>0){
			  page=input.val();
		  }else{
			  page=1;
		  }
	  }
	  var form=$("#"+formId);
	  form.append('<input type="hidden" name="page" value="'+page+'"/>')
//	  var action=baseUrl+"?page="+page;
//	  form.attr("action",action);
	  form.submit();
}

function initPage(id,totalPage,pageNumber,formId){
	var baseUrl = $("#"+formId).attr("action");
	   $("#"+id).pagination(totalPage,{
		   num_edge_entries:1,
		   current_page:(pageNumber-1),
			callback:function(index,ct){
				if(isNaN(index)==false){
					var page=index+1;
					pageSubmitForm(id,formId,baseUrl,page);
					return false;
				}
			}
	   });
	  
	   $("#gonu").on("keydown",function(e){
		   if(e.keyCode==109||e.keyCode==189){
			   return false;
		   }
		  
		 
	   });
	   $(".page-btn").one("click",function(){
		   pageSubmitForm(id,formId,baseUrl);
	   });
}

function getNoticeCount(){
	/*var url=self.location.href;
	if(url.indexOf("tologin")==-1&&url.indexOf("logout")==-1){
		Ajax.get("admin/robotresult/checkHasProcessed",function(res){
			if(res.data){
				$("#noticeCount").addClass("active");
				}else{
					$("#noticeCount").removeClass("active");
					}
			});
	}*/
	
}
/**
 * 切换左侧导航菜单
 * @returns
 */
function toggleMenuEvent(){
	/*$("body").on("click",".leftMenuBtn",function(){
		$(".jbolt_admin_nav.expansion").removeClass("expansion");
		$(".jbolt_admin_left_navs").toggleClass("allexpansion");
		var allexpansion=$(".jbolt_admin_left_navs").hasClass("allexpansion");
		if(allexpansion==false){
			initOpenLeftNav();
		}
		localStorage.setItem('allexpansion', allexpansion);
	});*/
	
	$("body").on("click",".leftMenuBtn",function(){
		$(".jbolt_admin_nav.expansion").removeClass("expansion");
		$(".jbolt_admin").toggleClass("hideMenu").toggleClass("normalMenu");
		var hideMenu=$(".jbolt_admin").hasClass("hideMenu");
		if(hideMenu==false){
			initOpenLeftNav();
		}
		localStorage.setItem('jbolt_hideMenu', hideMenu);
		resizeMorris();
	});
}
/**
 * 如果当前页面有morris图表 resize处理
 * @returns
 */
function resizeMorris(){
	setTimeout(function(){
		windowResize();
	}, 1000);
}
/**
 * 自动触发window的resize
 * @returns
 */
function windowResize(){
	 if(document.createEvent) {
         var event = document.createEvent("HTMLEvents");
         event.initEvent("resize", true, true);
         window.dispatchEvent(event);
     } else if(document.createEventObject) {
         window.fireEvent("onresize");
     }
}
/**
 * 返回上一页
 * @returns
 */
function goback(){
	history.go(-1);
}
/**
 * 刷新当前页
 * @returns
 */
function reloadCurrentPage(){
	history.go(0);
}
/**
 * 隐藏dialog按钮
 * @returns
 */
function hideParentLayerDialogBtn(index){
	parent.$(".layui-layer-btn").children().eq(index).hide();
}
/**
 * 隐藏Layer上的所有按钮
 * @returns
 */
function hideAllParentLayerDialogBtn(){
	parent.$(".layui-layer-btn").hide();
}
 
/**
 * 修改按钮标题
 * @returns
 */
function changeParentLayerDialogBtnTitle(index,btnTitle){
	parent.$(".layui-layer-btn").children().eq(index).text(btnTitle);
}
/**
 * 得到按钮
 * @param index
 * @returns
 */
function getParentLayerDialogBtn(index){
	  return parent.$(".layui-layer-btn").children().eq(index);
}
/**
 * 显示dialog按钮
 * @returns
 */
function showParentLayerDialogBtn(index){
	  if(index){
		  parent.$(".layui-layer-btn").children().eq(index).show();
	  }else{
		  parent.$(".layui-layer-btn").show();
	  }
	  
}
/**
 * 添加按钮
 * @param title
 * @param cssClass
 * @param clickFunc
 * @returns
 */
function addParentLayerDialogBtn(title,cssClass,clickFunc){
	var rId=randomId();
	var btnId="lay_btn_"+rId;
	var btn=$("<a id='"+btnId+"' class='"+cssClass+"'>"+title+"</a>");
	btn.on("click",function(e){
		e.preventDefault();
		e.stopPropagation();
		if(clickFunc){
			clickFunc();
		}
	});
	 parent.$(".layui-layer-btn").find("a.layui-layer-btn0").after(btn);
	 return btnId;
}

$(function(){
	//初始化左侧导航菜单
	initAdminLeftNav();
	getNoticeCount();
	var pjaxScript=$("#pjaxScript");
	var hasPjax=pjaxScript&&pjaxScript.length==1;
	if(needPjax&&hasPjax){
		//初始化全局pjax
		initAdminPjax();
		afterPjax();
		DragScrollUtil.init('dragScroll');
	}else{
		SelectUtil.autoLoad();
		SelectUtil.initAutoSetValue();
		JAtomFileUploadUtil.init();
		JAtomImgUploader.init();
		SwitchBtn.init();
		RadioUtil.init();
		FormDate.init();
		SummerNoteEditorUtil.init();
		CheckboxUtil.init();
		LayerPhotoUtil.init();
		LayerTipsUtil.init();
		$("[data-ajaxportal]").ajaxPortal();
		$("[tooltip]").tooltip();
		$('[data-toggle="tooltip"]').tooltip()
	}
	
	//初始化tab事件
	initTabs();
	//TableUtil init 初始化table列表里的edit del按钮
	TableUtil.init();
	PageOptUtil.init();
	toggleMenuEvent();
	
	});

