package com.zorona.liverooms.z_demo;

import com.zorona.liverooms.R;
import com.zorona.liverooms.models.Song_dummy;
import com.zorona.liverooms.models.User_dummy;
import com.zorona.liverooms.models.ChatUser_dummy;
import com.zorona.liverooms.models.Chat_dummy;
import com.zorona.liverooms.models.CoinPlan_dummy;
import com.zorona.liverooms.models.Comment_dummy;
import com.zorona.liverooms.models.GiftCategory_dummy;
import com.zorona.liverooms.models.GiftRoot_dummy;
import com.zorona.liverooms.models.Post_dummy;
import com.zorona.liverooms.models.Reels_dummy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Demo_contents {


    public static ArrayList<String> girlsImage = new ArrayList<>(Arrays.asList(
            "https://images.unsplash.com/photo-1506610154363-2e1a8c573d2d?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=844&q=80",
            "https://images.unsplash.com/photo-1555703473-5601538f3fd8?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=858&q=80",
            "https://images.unsplash.com/photo-1597983073453-ef06cfc2240e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=880&q=80",
            "https://images.unsplash.com/photo-1588671335940-2a6646b6bb0a?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=8getRandomPostCoint()&q=80",
            "https://images.unsplash.com/photo-1583058905141-deef2de746bb?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=888&q=80"

    ));

  /*  public static List<Sticker_dummy> getStickers() {
        List<Sticker_dummy> stickerDummies = new ArrayList<>();
        stickerDummies.add(new Sticker_dummy(1, "https://muly.starthub.ltd/storage/demo/stickers/tBYh155Uj846jNB.png"));
        stickerDummies.add(new Sticker_dummy(2, "https://muly.starthub.ltd/storage/demo/stickers/5xjouRhyJJul6vG.png"));
        stickerDummies.add(new Sticker_dummy(3, "https://muly.starthub.ltd/storage/demo/stickers/VQsIiRGJb1xyR29.png"));
        stickerDummies.add(new Sticker_dummy(4, "https://muly.starthub.ltd/storage/demo/stickers/uMupGAtXaI2Yzm6.png"));
        stickerDummies.add(new Sticker_dummy(5, "https://muly.starthub.ltd/storage/demo/stickers/6MRpnln3q8DMTuC.png"));
        stickerDummies.add(new Sticker_dummy(6, "https://muly.starthub.ltd/storage/demo/stickers/r6oSVjkVNY9Opww.png"));
        stickerDummies.add(new Sticker_dummy(7, "https://muly.starthub.ltd/storage/demo/stickers/rcKJ3JIuBT6JQkL.png"));
        stickerDummies.add(new Sticker_dummy(8, "https://muly.starthub.ltd/storage/demo/stickers/vtJsNlyEUZvqEQb.png"));
        stickerDummies.add(new Sticker_dummy(9, "https://muly.starthub.ltd/storage/demo/stickers/dvRToewsl0vliMw.png"));
        stickerDummies.add(new Sticker_dummy(10, "https://muly.starthub.ltd/storage/demo/stickers/9N2gUIUDdwTsPAT.png"));

        return stickerDummies;
    }*/

    public static ArrayList<String> boysImage = new ArrayList<>(Arrays.asList(
            "https://images.unsplash.com/photo-1609637082285-1aa1e1a63c16?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=880&q=80",
            "https://images.unsplash.com/photo-1485528562718-2ae1c8419ae2?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=858&q=80",
            "https://images.unsplash.com/photo-1552774021-9ebbb764f03e?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=880&q=80",
            "https://images.unsplash.com/photo-1629189858155-9eb2649ec778?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=880&q=80",
            "https://images.unsplash.com/photo-1570211776086-5836c8b1e75f?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=880&q=80"
    ));

    public static List<Song_dummy> getSongFiles() {
        List<Song_dummy> songDummies = new ArrayList<>();
        songDummies.add(new Song_dummy(1, "Rahogi Meri", "Pritam, Arijit Singh",
                "https://muly.starthub.ltd/storage/demo/covers/BydL9iUJ1wRZAgYpng",
                "https://muly.starthub.ltd/storage/demo/audios/jrmyRx4Uwy3GkVy.aac", 14, ""));

        songDummies.add(new Song_dummy(2, "Coca Cola", "Pritam, Arijit Singh",
                "https://muly.starthub.ltd/storage/demo/covers/ZFpka7K6dxUAQCnpng",
                "https://muly.starthub.ltd/storage/demo/audios/jrmyRx4Uwy3GkVy.aac", 19, ""));

        songDummies.add(new Song_dummy(3, "Savage Love (Laxed - Siren Beat)", "Jawsh 685, Jason Derulo",
                "https://muly.starthub.ltd/storage/demo/covers/ZEybCPyhf0QcUZZpng",
                "https://muly.starthub.ltd/storage/demo/audios/93BahZERK0DOiiq.aac", 28, ""));

        songDummies.add(new Song_dummy(4, "Thumbi Thullal", "A. R. Rahman",
                "https://muly.starthub.ltd/storage/demo/covers/pU59tYWwgzC6Hi5png",
                "https://muly.starthub.ltd/storage/demo/audios/S3XXGz6YoTWwvaZ.aac", getRandomPostCoint(), ""));

        songDummies.add(new Song_dummy(5, "For the Night", "Pop Smoke, Lil Baby & DaBaby",
                "https://muly.starthub.ltd/storage/demo/covers/6XyPuIdF3PJmEICpng",
                "https://muly.starthub.ltd/storage/demo/audios/93BahZERK0DOiiq.aac", getRandomPostCoint(), ""));


        return songDummies;
    }

    public static List<String> girlsBio() {
        List<String> bios = new ArrayList<>();

        String bio1 = "Money can’t buy happiness. But it can buy Makeup!";
        String bio2 = "Sometimes it’s the princess who kills the dragon and saves the prince.";
        String bio3 = "love..dancing.\uD83D\uDE18\uD83D\uDE18\n" +
                "luv ❤my❤ friends\uD83D\uDC48";
        String bio4 = "\uD83D\uDCF7Like Photography✔\n" +
                "\uD83D\uDC01Animal Lover✔\n" +
                "\uD83C\uDF6CChocolate Lover✔\n" +
                "\uD83D\uDE2DFirst Cry On 11th March✔\n" +
                "\uD83D\uDC8AMedical Student✔\n";
        String bio5 = "I’m a princess \uD83D\uDC96, not because I have a Prince, but because my dad is a king \uD83D\uDC51\n";

        bios.add(bio1);
        bios.add(bio2);
        bios.add(bio3);
        bios.add(bio4);
        bios.add(bio5);

        return bios;
    }

    public static List<String> boysBio() {
        List<String> bios = new ArrayList<>();

        String bio1 = "\uD83D\uDCAFOfficial account\uD83D\uDD10\n" +
                "\uD83D\uDCF7Photography\uD83D\uDCF7\n" +
                "\uD83D\uDE18Music lover\uD83C\uDFB6\n" +
                "⚽Sports lover⛳\n" +
                "\uD83D\uDE0DSports bike lover\n";
        String bio2 = "\uD83D\uDC51Attitude Prince\uD83D\uDC51\n" +
                "\uD83E\uDD1DDosto Ki Shan\uD83D\uDC9C\n" +
                "\uD83D\uDC8FGF Ki Jaan♥️\n" +
                "\uD83D\uDC9EMom Ka Ladla\uD83D\uDC93\n" +
                "\uD83D\uDC99Pappa Ka Pyara\uD83D\uDC99";
        String bio3 = "love..dancing.\uD83D\uDE18\uD83D\uDE18\n" +
                "luv ❤my❤ friends\uD83D\uDC48";
        String bio4 = "\uD83D\uDCF7Like Photography✔\n" +
                "\uD83D\uDC01Animal Lover✔\n" +
                "\uD83C\uDF6CChocolate Lover✔\n" +
                "\uD83D\uDE2DFirst Cry On 11th March✔\n" +
                "\uD83D\uDC8AMedical Student✔\n";
        String bio5 = "༺❉MR. Perfect❉༻\n" +
                "\uD83D\uDCA5King OF 22 May\uD83C\uDF1F\n" +
                "\uD83C\uDFB5Music Addicted\uD83C\uDFB6\n" +
                "\uD83D\uDC9C Photography\uD83D\uDCF8\n" +
                "\uD83D\uDC95Heart Hã¢Kër\uD83D\uDC8C";

        bios.add(bio1);
        bios.add(bio2);
        bios.add(bio3);
        bios.add(bio4);
        bios.add(bio5);

        return bios;
    }

    public static List<User_dummy> getUsers(boolean isShuffle) {


        List<User_dummy> userDummies = new ArrayList<>(Arrays.asList(
                new User_dummy("Aaliya Mia", girlsBio().get(0), "@aaliya1", "aaliyamia@email.com", girlsImage.get(0), "USA",
                        1, getRandomCoin(), getRandomCoin(), getRandomPostCoint(), getRandomPostCoint(), getRandomCoin(), getRandomPostCoint(), getRandomCoin(), "FEMALE"),

                new User_dummy("Lily Adams", girlsBio().get(1), "@Lily", "Lily@email.com", girlsImage.get(1), "UK",
                        2, getRandomCoin(), getRandomCoin(), getRandomPostCoint(), getRandomPostCoint(), getRandomCoin(), getRandomPostCoint(), getRandomCoin(), "FEMALE"),

                new User_dummy("Charlotte Bailey", girlsBio().get(2), "@Charlotte", "Bailey@email.com", girlsImage.get(2), "GERMANY",
                        3, getRandomCoin(), getRandomCoin(), getRandomPostCoint(), getRandomPostCoint(), getRandomCoin(), getRandomPostCoint(), getRandomCoin(), "FEMALE"),

                new User_dummy("Isabella Kennedy", girlsBio().get(3), "@Isabella", "Kennedy@email.com", girlsImage.get(3), "INDIA",
                        4, getRandomCoin(), getRandomCoin(), getRandomPostCoint(), getRandomPostCoint(), getRandomCoin(), getRandomPostCoint(), getRandomCoin(), "FEMALE"),

                new User_dummy("Camila Marshall", girlsBio().get(4), "@Camila", "Marshall@email.com", girlsImage.get(4), "FRANCE",
                        5, getRandomCoin(), getRandomCoin(), getRandomPostCoint(), getRandomPostCoint(), getRandomCoin(), getRandomPostCoint(), getRandomCoin(), "FEMALE"),
                //,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,

                new User_dummy("James Carter", boysBio().get(0), "@Carter", "James@email.com", boysImage.get(0), "USA",
                        1, getRandomCoin(), getRandomCoin(), getRandomPostCoint(), getRandomPostCoint(), getRandomCoin(), getRandomPostCoint(), getRandomCoin(), "MALE"),

                new User_dummy("Henry Adams", boysBio().get(1), "@Henry", "Adams@email.com", boysImage.get(1), "FRANCE",
                        2, getRandomCoin(), getRandomCoin(), getRandomPostCoint(), getRandomPostCoint(), getRandomCoin(), getRandomPostCoint(), getRandomCoin(), "MALE"),

                new User_dummy("Daniel Davidson", boysBio().get(2), "@Davidson", "Daniel@email.com", boysImage.get(2), "INDIA",
                        3, getRandomCoin(), getRandomCoin(), getRandomPostCoint(), getRandomPostCoint(), getRandomCoin(), getRandomPostCoint(), getRandomCoin(), "MALE"),

                new User_dummy("Jackson Edwards", boysBio().get(3), "@Jackson", "Edwards@email.com", boysImage.get(3), "GERMANY",
                        4, getRandomCoin(), getRandomCoin(), getRandomPostCoint(), getRandomPostCoint(), getRandomCoin(), getRandomPostCoint(), getRandomCoin(), "MALE"),

                new User_dummy("Thomas Bailey", boysBio().get(4), "@Thomas", "Bailey@email.com", boysImage.get(4), "UK",
                        5, getRandomCoin(), getRandomCoin(), getRandomPostCoint(), getRandomPostCoint(), getRandomCoin(), getRandomPostCoint(), getRandomCoin(), "MALE")

        ));
        if (isShuffle) {
            Collections.shuffle(userDummies);
        }
        return userDummies;
    }

    public static int getRandomCoin() {
        Random random = new Random();
        int i = random.nextInt(1000 - 111) + 111;
        return i;
    }

    public static int getRandomPostCoint() {
        Random random = new Random();
        int i = random.nextInt(100 - 11) + 11;
        return i;
    }

   /* public static List<LiveStramComment> getLiveStreamComment() {
        List<LiveStramComment> liveStramCommentDummies = new ArrayList<>();

        liveStramCommentDummies.add(new LiveStramComment("", getUsers(true).get(0), true));
        liveStramCommentDummies.add(new LiveStramComment("", getUsers(true).get(0), true));
        liveStramCommentDummies.add(new LiveStramComment("", getUsers(true).get(0), true));
        liveStramCommentDummies.add(new LiveStramComment("", getUsers(true).get(0), true));
        liveStramCommentDummies.add(new LiveStramComment("", getUsers(true).get(0), true));
        liveStramCommentDummies.add(new LiveStramComment("Please stop looking so hot every time.", getUsers(true).get(0), false));
        liveStramCommentDummies.add(new LiveStramComment("Looking very very hot\uD83D\uDD25in summer", getUsers(true).get(0), false));
        liveStramCommentDummies.add(new LiveStramComment("Your queenly smiles are what my eyes have been longing to see.", getUsers(true).get(0), false));
        liveStramCommentDummies.add(new LiveStramComment("Too hot for me to handle", getUsers(true).get(0), false));
        liveStramCommentDummies.add(new LiveStramComment("Every single part of your body was made according to my spec.", getUsers(true).get(0), false));
        liveStramCommentDummies.add(new LiveStramComment("I drop my cap for you.", getUsers(true).get(0), false));
        liveStramCommentDummies.add(new LiveStramComment("Your hotness is just beating me everytim.", getUsers(true).get(0), false));
        liveStramCommentDummies.add(new LiveStramComment("Classy shot and awesome background too.", getUsers(true).get(0), false));
        liveStramCommentDummies.add(new LiveStramComment("Hello dear,", getUsers(true).get(0), false));
        liveStramCommentDummies.add(new LiveStramComment("Give me your mobile number", getUsers(true).get(0), false));
        liveStramCommentDummies.add(new LiveStramComment("9975537455 it is my mobile number", getUsers(true).get(0), false));
        Collections.shuffle(liveStramCommentDummies);
        return liveStramCommentDummies;

    }*/

    public static List<Post_dummy> getPosts() {
        List<Post_dummy> postDummies = new ArrayList<>();

        postDummies.add(new Post_dummy("Surat India", "2 hours ago", "You can regret a lot of things but you’ll never regret being kind",
                "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1170&q=80"
                , getUsers(true).get(0), getRandomPostCoint(), getRandomPostCoint()));
        postDummies.add(new Post_dummy("Athens Greece", "1 hours ago", "Life’s a beach",
                "https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1170&q=80"
                , getUsers(true).get(0), getRandomPostCoint(), getRandomPostCoint()));
        postDummies.add(new Post_dummy("Jakarta Indonesia", "10 hours ago", "Dreams don’t have expiration dates, keep going",
                "https://images.unsplash.com/photo-1586105449897-20b5efeb3233?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1170&q=80"
                , getUsers(true).get(0), getRandomPostCoint(), getRandomPostCoint()));
        postDummies.add(new Post_dummy("Rome Italy", "10 May 2021", "If you don’t believe in yourself, who will?",
                "https://images.unsplash.com/photo-1546006508-5bd647796a4c?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1170&q=80"
                , getUsers(true).get(0), getRandomPostCoint(), getRandomPostCoint()));
        postDummies.add(new Post_dummy("Abuja Nigeria", "12 June 2021", "The biggest mistake you can ever make is to be afraid to make mistakes",
                "https://images.unsplash.com/photo-1600854109241-46990389fb97?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1164&q=80"
                , getUsers(true).get(0), getRandomPostCoint(), getRandomPostCoint()));
        postDummies.add(new Post_dummy("Manila India", "2 minutes ago", "I don’t tell you I love you out of habit but as a reminder of how much you mean to me",
                "https://images.unsplash.com/photo-1468818438311-4bab781ab9b8?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1171&q=80"
                , getUsers(true).get(0), getRandomPostCoint(), getRandomPostCoint()));
        postDummies.add(new Post_dummy("Moscow Russia", "22 hours ago", "Don’t trust everything you see, even salt can look like sugar",
                "https://images.unsplash.com/photo-1524496686051-f9b8e8cf9d1a?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1170&q=80"
                , getUsers(true).get(0), getRandomPostCoint(), getRandomPostCoint()));
        postDummies.add(new Post_dummy("Singapore", "21 MAy 2021", "Pursue your passion and you’ll never work a day in your life",
                "https://images.unsplash.com/photo-1455906876003-298dd8c44ec8?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1676&q=80"
                , getUsers(true).get(0), getRandomPostCoint(), getRandomPostCoint()));
        postDummies.add(new Post_dummy("Bangkok Thailand", "2 Minutes ago", "My life is as crooked as Rami Malek’s bowtie",
                "https://images.unsplash.com/photo-1546882304-ebdc87c20a38?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1169&q=80"
                , getUsers(true).get(0), getRandomPostCoint(), getRandomPostCoint()));
        postDummies.add(new Post_dummy("Abu Dhabi UAE", "Just Now", "Decluttering my life like Marie Kondo",
                "https://images.unsplash.com/photo-1593186508532-cba7ccf23e5a?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1170&q=80"
                , getUsers(true).get(0), getRandomPostCoint(), getRandomPostCoint()));
        postDummies.add(new Post_dummy("Paris France", "Just Now", "Do whatever makes you happiest",
                "https://images.unsplash.com/photo-1614929511547-974944a54c92?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80"
                , getUsers(true).get(0), getRandomPostCoint(), getRandomPostCoint()));
        Collections.shuffle(postDummies);
        return postDummies;
    }

    public static List<Comment_dummy> getComments() {
        List<Comment_dummy> commentDummies = new ArrayList<>();
        commentDummies.add(new Comment_dummy(getUsers(true).get(0), "2 hour ago", "No matter where I go, I cannot find someone beautiful like you."));
        commentDummies.add(new Comment_dummy(getUsers(true).get(0), "Just Now", "❤️Your looks make me insane.❤️"));
        commentDummies.add(new Comment_dummy(getUsers(true).get(0), "5 hour ago", "Such a charming capture✔️✔️✔️✔️"));
        commentDummies.add(new Comment_dummy(getUsers(true).get(0), "12 hour ago", "Well I think this is often my favorite posture of yours"));
        commentDummies.add(new Comment_dummy(getUsers(true).get(0), "2 minutes ago", "\uD83D\uDC9BHow can somebody be this beautiful\uD83D\uDC9B"));
        commentDummies.add(new Comment_dummy(getUsers(true).get(0), "5 minutes ago", "❤️We all are favored to see your magnificence.❤️"));
        commentDummies.add(new Comment_dummy(getUsers(true).get(0), "22 May 2021", "This is amazing."));
        commentDummies.add(new Comment_dummy(getUsers(true).get(0), "2 Sep 2021", "Omg!! your look steals my heart"));
        commentDummies.add(new Comment_dummy(getUsers(true).get(0), "12 Oct 2021", "⊂◉‿◉つ\n" +
                "– \\ \uD83D\uDC49 \\ \uD83D\uDC49 Nice.. pics Miss\n" +
                "\uD83D\uDC49This is Awesome Pic\n" +
                "\uD83D\uDC49 by \uD83C\uDF37 Anurag \uD83C\uDF37"));


        Collections.shuffle(commentDummies);
        return commentDummies;
    }

    public static List<Reels_dummy> getReels() {

        List<Reels_dummy> reels = new ArrayList<>();

        reels.add(new Reels_dummy(getUsers(true).get(0), "Creativity is my thing, what's yours?", "https://dev.digicean.com/storage/239891192_3061111304125653_779170672042824055_n.mp4", getRandomPostCoint(), getRandomPostCoint()));
        reels.add(new Reels_dummy(getUsers(true).get(0), "Creativity is my thing, what's yours?", "https://dev.digicean.com/storage/241681828_894992998067015_8734076864715268332_n.mp4", getRandomPostCoint(), getRandomPostCoint()));
        reels.add(new Reels_dummy(getUsers(true).get(0), "Life is better when you're laughing.", "https://dev.digicean.com/storage/242025003_129510119414535_2313267097805870250_n.mp4", getRandomPostCoint(), getRandomPostCoint()));
        reels.add(new Reels_dummy(getUsers(true).get(0), "If you like this video, it's supposed to bring you good luck for the rest of the day.", "https://dev.digicean.com/storage/242025003_129510119414535_2313267097805870250_n.mp4", getRandomPostCoint(), getRandomPostCoint()));
        reels.add(new Reels_dummy(getUsers(true).get(0), "If life had a soundtrack, this would be my song.", "https://dev.digicean.com/storage/247062863_341024567823973_5333055093789086258_n.mp4", getRandomPostCoint(), getRandomPostCoint()));
        reels.add(new Reels_dummy(getUsers(true).get(0), "I reel-y got into this whole Instagram Reels thing.", "https://dev.digicean.com/storage/254729432_1108864849935754_4352344584780102107_n.mp4", getRandomPostCoint(), getRandomPostCoint()));
        reels.add(new Reels_dummy(getUsers(true).get(0), "I'm not going to tell you how long it took me to edit this.", "https://dev.digicean.com/storage/255030357_230359622521878_8463246573248024146_n.mp4", getRandomPostCoint(), getRandomPostCoint()));

        reels.add(new Reels_dummy(getUsers(true).get(0), "It's me, the best dancer on your feed.", "https://dev.digicean.com/storage/247062863_341024567823973_5333055093789086258_n.mp4", getRandomPostCoint(), getRandomPostCoint()));
        reels.add(new Reels_dummy(getUsers(true).get(0), "If I’m on your feed, you know it’s gonna be a good day.", "https://dev.digicean.com/storage/241681828_894992998067015_8734076864715268332_n.mp4", getRandomPostCoint(), getRandomPostCoint()));
        reels.add(new Reels_dummy(getUsers(true).get(0), "Wedding", "https://dev.digicean.com/storage/255030357_230359622521878_8463246573248024146_n.mp4", getRandomPostCoint(), getRandomPostCoint()));
        reels.add(new Reels_dummy(getUsers(true).get(0), "Beauty of Bride", "https://dev.digicean.com/storage/239891192_3061111304125653_779170672042824055_n.mp4", getRandomPostCoint(), getRandomPostCoint()));


        Collections.shuffle(reels);
        return reels;
    }

    public static List<ChatUser_dummy> getChatUsers() {
        List<ChatUser_dummy> chatUserDummies = new ArrayList<>();

        chatUserDummies.add(new ChatUser_dummy(getUsers(false).get(0), "2 hour ago", "Hello"));
        chatUserDummies.add(new ChatUser_dummy(getUsers(false).get(1), "Just Now", "Hey Can we talk ?"));
        chatUserDummies.add(new ChatUser_dummy(getUsers(false).get(2), "Just Now", "I am available now"));
        chatUserDummies.add(new ChatUser_dummy(getUsers(false).get(3), "5 hour ago", "Hello Dear I am Isabella"));
        chatUserDummies.add(new ChatUser_dummy(getUsers(false).get(4), "12 hour ago", ""));
        chatUserDummies.add(new ChatUser_dummy(getUsers(false).get(5), "2 minutes ago", "Whats up baby."));
        chatUserDummies.add(new ChatUser_dummy(getUsers(false).get(6), "5 minutes ago", "Have a nice day"));
        chatUserDummies.add(new ChatUser_dummy(getUsers(false).get(7), "22 May 2021", "Can we talk later?"));
        chatUserDummies.add(new ChatUser_dummy(getUsers(false).get(8), "2 Sep 2021", "Where are you from?"));
        chatUserDummies.add(new ChatUser_dummy(getUsers(false).get(9), "12 Oct 2021", "Hello, I am Thomas"));

        Collections.shuffle(chatUserDummies);
        return chatUserDummies;
    }

    public static List<Chat_dummy> getRandomChat() {
        List<Chat_dummy> chatDummies = new ArrayList<>();
        chatDummies.add(new Chat_dummy("What's yor name ?", Chat_dummy.TEXT, Chat_dummy.USER1));
        chatDummies.add(new Chat_dummy("Hey do you want chat with me ?", Chat_dummy.TEXT, Chat_dummy.USER1));
        chatDummies.add(new Chat_dummy("Hmm", Chat_dummy.TEXT, Chat_dummy.USER1));
        chatDummies.add(new Chat_dummy("What ?", Chat_dummy.TEXT, Chat_dummy.USER1));
        chatDummies.add(new Chat_dummy("Are you kidding with me?", Chat_dummy.TEXT, Chat_dummy.USER1));
        chatDummies.add(new Chat_dummy("Do  you have  girlfriend?", Chat_dummy.TEXT, Chat_dummy.USER1));
        chatDummies.add(new Chat_dummy("hey Dude I am boring now \n so you can chat with me", Chat_dummy.TEXT, Chat_dummy.USER1));
        chatDummies.add(new Chat_dummy("I am busy right now \n talk to you later", Chat_dummy.TEXT, Chat_dummy.USER1));
        chatDummies.add(new Chat_dummy("Send me your insta id", Chat_dummy.TEXT, Chat_dummy.USER1));
        chatDummies.add(new Chat_dummy("Send me your Number", Chat_dummy.TEXT, Chat_dummy.USER1));
        chatDummies.add(new Chat_dummy("Am i looking Sexy??", Chat_dummy.TEXT, Chat_dummy.USER1));


        //Collections.shuffle(chatDummies);
        return chatDummies;
    }

    public static List<CoinPlan_dummy> getCoinList() {
        List<CoinPlan_dummy> coinPlans = new ArrayList<>();
        coinPlans.add(new CoinPlan_dummy(100, 10, ""));
        coinPlans.add(new CoinPlan_dummy(200, 20, ""));
        coinPlans.add(new CoinPlan_dummy(1000, 90, "10% off"));
        coinPlans.add(new CoinPlan_dummy(10000, 800, "20% off"));
        coinPlans.add(new CoinPlan_dummy(50000, 2500, "50% off"));
        return coinPlans;
    }

    public static List<String> getFemaleVideos() {
        List<String> videos = new ArrayList<>(Arrays.asList(
                "https://dev.digicean.com/storage/1614063597527.mp4",
                "https://dev.digicean.com/storage/1%20(14).mp4",
                "https://dev.digicean.com/storage/1%20(22).mp4",
                "https://dev.digicean.com/storage/1%20(5).mp4",
                "https://dev.digicean.com/storage/1%20(4).mp4"
        ));
        Collections.shuffle(videos);
        return videos;
    }

    public static List<String> getHashtags() {
        List<String> videos = new ArrayList<>(Arrays.asList(
                "#Love", "#Nature", "#Wedding", "#Alone", "#Female", "#Chill", "#Beauty", "#Life", "#Honeymoon", "#Style", "#Happy", "#Smile", "#Music", "#Sunset", "#Sport"
        ));
        Collections.shuffle(videos);
        return videos;
    }

    public static List<String> getLocations() {
        List<String> videos = new ArrayList<>(Arrays.asList(
                "Surat, India", "London, England", "Paris, France", "New York City, United States", "Moscow, Russia",
                "Tokyo, Japan", "Los Angeles, United States", " Barcelona, Spain"
                , "Madrid, Spain", "Rome, Italy", "Doha, Qatar", "Chicago, United States", "Abu Dhabi, UAE"
                , "San Francisco, US", "Amsterdam, Netherlands", "Delhi, India ", "Mumbai, India", "Bangalore, India"
        ));
        Collections.shuffle(videos);
        return videos;
    }

    public static List<GiftCategory_dummy> getGiftCategory() {
        List<GiftRoot_dummy> emoji = new ArrayList<>(Arrays.asList(
                new GiftRoot_dummy(1, R.raw.emoji, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(2, R.raw.emoji1, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(3, R.raw.emoji2, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(6, R.raw.party, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(7, R.raw.star, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(8, R.raw.wink, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(9, R.raw.wow, 10, GiftRoot_dummy.IMAGE)


        ));
        List<GiftRoot_dummy> love = new ArrayList<>(Arrays.asList(
                new GiftRoot_dummy(5, R.raw.heart, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(16, R.raw.s116, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(13, R.raw.s113, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(14, R.raw.s114, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(18, R.raw.s118, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(33, R.raw.srose1, 10, GiftRoot_dummy.IMAGE)


        ));
        List<GiftRoot_dummy> sticker = new ArrayList<>(Arrays.asList(
                new GiftRoot_dummy(4, R.raw.g_fox, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(10, R.raw.s110, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(11, R.raw.s111, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(15, R.raw.s115, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(17, R.raw.s117, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(19, R.raw.s119, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(20, R.raw.s120, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(12, R.raw.s112, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(21, R.raw.s121, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(22, R.raw.s122, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(23, R.raw.s123, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(24, R.raw.s124, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(25, R.raw.s125, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(26, R.raw.s126, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(27, R.raw.s127, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(28, R.raw.s128, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(29, R.raw.s129, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(30, R.raw.srose, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(31, R.raw.s130, 10, GiftRoot_dummy.IMAGE),
                new GiftRoot_dummy(32, R.raw.s131, 10, GiftRoot_dummy.IMAGE)


        ));
        Collections.shuffle(love);
        Collections.shuffle(emoji);
        Collections.shuffle(sticker);

        List<GiftCategory_dummy> giftCategories = new ArrayList<>();
        giftCategories.add(new GiftCategory_dummy("Sticker", sticker));
        giftCategories.add(new GiftCategory_dummy("Emoji", emoji));
        giftCategories.add(new GiftCategory_dummy("Love", love));

        //sticker

        // Collections.shuffle(list);
        return giftCategories;
    }

}
