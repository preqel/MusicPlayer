package com.example.helpers.lastfm;

import java.util.ArrayList;
import java.util.Collection;

import com.example.helpers.DomElement;
 

public abstract class MusicEntry extends ImageHolder {
    protected String name;
    protected String url;
    protected String mbid;
    protected String id;
    /**
     * This property is only available on hype charts, like
     * {@link Chart#getHypedArtists(String)} or
     * {@link de.umass.lastfm.Group#getHype(String, String)}
     */
    protected int percentageChange;
    protected Collection<String> tags = new ArrayList<String>();
    protected MusicEntry(String name, String url) {
        this(name, url, null, -1, -1, false);
    }
    
    protected MusicEntry(String name, String url, String mbid, int playcount, int listeners, boolean streamable) {
        this.name = name;
        this.url = url;
        this.mbid = mbid;
    }

    public String getMbid() {
        return mbid;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public Collection<String> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + "name='" + name + '\'' + ", id='" + id
                + '\'' + ", url='" + url + '\'' + ", mbid='" + mbid + '\'' + ']';
    }

    /**
     * Loads all generic information from an XML <code>DomElement</code> into
     * the given <code>MusicEntry</code> instance, i.e. the following tags:<br/>
     * <ul>
     * <li>playcount/plays</li>
     * <li>listeners</li>
     * <li>streamable</li>
     * <li>name</li>
     * <li>url</li>
     * <li>mbid</li>
     * <li>image</li>
     * <li>tags</li>
     * </ul>
     * 
     * @param entry An entry
     * @param element XML source element
     */
    protected static void loadStandardInfo(MusicEntry entry, DomElement element) { //����
        entry.name = element.getChildText("name");
        entry.url = element.getChildText("url");
        entry.mbid = element.getChildText("mbid");
        ImageHolder.loadImages(entry, element); //ͼƬ����
    }
}
