KindEditor.ready(function (K) {
    window.editor = K.create('#content', {
        resizeType: '0',
        width: '100%',
        height: '600px',
        uploadJson: 'ajaxEditorFileUpload',
        afterBlur: function () { this.sync(); }
    });
});

        //        //获取粘贴事件
        //        $body.addEventListener( 'paste', function( e ){
        //            window.clipboardData = e.clipboardData;
        //            var i = 0, items, item, types;
        //            if( clipboardData ){
        //                items = clipboardData.items;
        //                if( !items ){
        //                    return;
        //                }
        //                item = items[0];
        //                types = clipboardData.types || [];
        //                for( ; i < types.length; i++ ){
        //                    if( types[i] === 'Files' ){
        //                        item = items[i];
        //                        break;
        //                    }
        //                }
        //                if( item && item.kind === 'file' && item.type.match(/^image\//i) ){
        //                    //上传图片
        //                    imgReader( item );
        //                }
        //
        //            }
        //        });
        //    })();
        //},

