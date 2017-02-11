package com.example.helpers.lastfm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.example.helpers.DomElement;

public abstract class ImageHolder {

	
	protected Map<ImageSize,String> imageUrls =new HashMap<ImageSize,String>();


    public String getLargeImage(){
    	Set<ImageSize> sets  = availableSizes();
    	if(sets.contains(ImageSize.MEGA))
    		return getImageURL(ImageSize.MEGA);
    	else if(sets.contains(ImageSize.EXTRALARGE))
    		return getImageURL(ImageSize.EXTRALARGE);
    	else if(sets.contains(ImageSize.LARGE))
    		return getImageURL(ImageSize.LARGE);
    	else if(sets.contains(ImageSize.MEDIUM))
    		return getImageURL(ImageSize.MEDIUM);
    	else  
    		return getImageURL(ImageSize.LARGE);
    	
    }

    protected static void loadImages(ImageHolder holder, DomElement element) {
        Collection<DomElement> images = element.getChildren("image");
        for (DomElement image : images) {
            String attribute = image.getAttribute("size");
            ImageSize size = null;
            if (attribute == null) {
                size = ImageSize.LARGE; 
            } else {
                try {
                    size = ImageSize.valueOf(attribute.toUpperCase(Locale.ENGLISH));
                } catch (IllegalArgumentException e) {
                }
            }
            if (size != null)
                holder.imageUrls.put(size, image.getText());
        }
    }
    

	private Set<ImageSize> availableSizes() {
		return imageUrls.keySet();
	}


	private String getImageURL(ImageSize mega) {
		return imageUrls.get(mega);
	}

}
