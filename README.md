# Tooltipnator

This is a tooltip library like you've never seen before.

[![](https://jitpack.io/v/rhychel/tooltipnator.svg)](https://jitpack.io/#rhychel/tooltipnator)

To build the tooltip dialog:
```java
    val tooltipDialog = TooltipDialog.Builder(this@MainActivity)  
	    // OPTIONAL!!! Use this if you only have a custom layout
        .textContentLayout(R.layout.sample_tooltip_text_content) 
        // OPTIONAL!!! Listener to detect when it tooltip overlay is closed
        .onTooltipClosedListener {   
        }
        // OPTIONAL!!! You can ignore this if you don't have a custom layout with button
        .closeButtonId(R.id.yourCustomButtonId)
        // OPTIONAL!!! When a custom layout or the predefined layout is loaded, this is called.
        .onContentLoadedListener { view, tooltipDialog,index ->  
            
        }
        .build()
```
You can use the tooltip dialog to show:

 1. Simple Text Tooltip
```java 
    tooltipDialog.showTextTooltipDialog(targetView, TooltipMaskShape.CIRCLE)
  ```

 2. Sequenced Tooltips
 ```java
   tooltipDialog.showSequenceTooltipDialog(
	   mutableListOf(  
			   TooltipDialogItem( 
				   buttonTargetView,
				   TooltipMaskShape.RECTANGLE
			   ),
			   TooltipDialogItem( 
				   fabTargetView,
				   TooltipMaskShape.CIRCLE
			   ),
			   TooltipDialogItem( 
				   findViewById(R.id.mTarget), // Menu Item
				   TooltipMaskShape.CIRCLE
			   )
	   )
   )
 ```

The predefined `ids` are:
```xml
<item name="layoutPointerTop" type="id"/> <!-- ID of pointer at the top of dialog --> 
<item name="layoutPointerBottom" type="id"/> <!-- ID of pointer at the bottom of dialog --> 

<item name="tvTextContent" type="id"/> <!-- ID of textView from the predefined text dialog --> 
<item name="tvSequenceTextContent" type="id"/> <!-- ID of textview from the predefined sequenced dialog --> 
  
<item name="btnDialogBack" type="id"/> <!-- Use this ID for your custom back button to show previous tooltip dialog (for sequenced). --> 
<item name="btnDialogNext" type="id"/> <!-- Use this ID for your custom next button to show next tooltip dialog (for sequenced). -->   
<item name="btnDialogClose" type="id"/> <!-- Use this ID for your custom close button to dismiss tooltip dialog (for text and sequenced). --> 
  
<item name="flTooltip" type="id"/> <!-- ID of framelayout containing the tooltip dialogs --> 
```

Instead of calling `TooltipDialog.Builder().textContentLayout` to override layout with a different layout id, you can simple create a layout file `tooltip_text_content,xml` or `tooltip_sequence_content.xml` to override layout.

The predefined `dimensions, colors` are:
```xml
 <dimen name="tooltip_pointer_width">20dp</dimen>  
 <dimen name="tooltip_pointer_height">8dp</dimen>  
  
 <dimen name="tooltip_layout_content_margin_start">8dp</dimen>  
 <dimen name="tooltip_layout_content_margin_end">8dp</dimen>  
 
 <color name="tooltipDialogBackgroundColor">#f5f5f5</color>
```

You can resource override the tooltip image by placing a drawable image with filename `ic_tooltip_pointer.png`.