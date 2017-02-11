package com.example.laibomusic;

interface IMusicService
{

    void openFile(String path);                    //���ļ�-ɨ���ý���ȡ���ظ���
    void open(in long [] list, int position);      //�򿪣��г���λ�ã�
    void stop();                                   //ֹͣ
    void pause();                                 //��ͣ
    void play();                                  //����
    void prev();                                  //��һ��
    void next();                                  //��һ��
    boolean isplaying();   
    long duration();                       //��ȡ�ܹ��Ĳ���ʱ��
    void enqueue(in long [] list, int action);   //�Ŷ�
    long [] getQueue();                          //��ȡ����
    void setQueuePosition(int index);            //���ö���λ��
    long seekTo(long pos);
    long getArtistId();                          //��ȡ������id
    long getAudioId();                           //��ȡ��Ƶid
    int getQueuePosition();                       //
    String getArtistName();                      //��ȡ��������
    String getAlbumName();                       //��ȡר����
     int  removeTrack(long trackid);                        //ɾ��track
     String getTrackName();                       //���ڲ��ŵ���������
     
     void setRepeatMode(int repeatmode);
    int getRepeatMode();                             //����ظ�ģʽ
    void toggleFavorite();
    //boolean isFavorite();
    boolean isFavorite(long id);
    void addToFavorites();
    void removeFromFavorites();
    
    
}