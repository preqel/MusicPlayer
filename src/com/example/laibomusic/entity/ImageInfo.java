package com.example.laibomusic.entity;

public class ImageInfo {
	public String type; //图片类型的夺取 ：专辑、艺术家、播放列表、流派

	public String source; //图片被拖来的地方:lastFM-web;file-audio file;gallery-会被传递的图片路径；first_avail-lastfm

	public String size; //图片的形状:thumb、normal

	public String[] data; //需要额外的数据来使图片好看:lastFM ：艺术家-艺术家图片、专辑和艺术家-专辑图片；file：专辑id;gallery：图片的文件路径；first_available：文件和lastFM的数据
}
