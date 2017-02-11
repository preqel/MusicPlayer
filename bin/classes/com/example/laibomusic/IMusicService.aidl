package com.example.laibomusic;

interface IMusicService
{

    void openFile(String path);                    //打开文件-扫描多媒体获取本地歌曲
    void open(in long [] list, int position);      //打开（列出、位置）
    void stop();                                   //停止
    void pause();                                 //暂停
    void play();                                  //播放
    void prev();                                  //上一首
    void next();                                  //下一首
    boolean isplaying();   
    long duration();                       //获取总共的播放时间
    void enqueue(in long [] list, int action);   //排队
    long [] getQueue();                          //获取队列
    void setQueuePosition(int index);            //设置队列位置
    long seekTo(long pos);
    long getArtistId();                          //获取艺术家id
    long getAudioId();                           //获取音频id
    int getQueuePosition();                       //
    String getArtistName();                      //获取艺术家名
    String getAlbumName();                       //获取专辑名
     int  removeTrack(long trackid);                        //删除track
     String getTrackName();                       //正在播放的音乐名称
     
     void setRepeatMode(int repeatmode);
    int getRepeatMode();                             //获得重复模式
    void toggleFavorite();
    //boolean isFavorite();
    boolean isFavorite(long id);
    void addToFavorites();
    void removeFromFavorites();
    
    
}