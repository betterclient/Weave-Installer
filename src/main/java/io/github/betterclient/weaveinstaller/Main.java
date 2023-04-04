package io.github.betterclient.weaveinstaller;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        File createModsFolder = new File(System.getProperty("user.home") + "\\.lunarclient\\mods\\");
        createModsFolder.mkdir();

        File folderPutInjectorAndStuff = new File("injector");
        folderPutInjectorAndStuff.mkdir();

        OS operatinSystem = null;

        if(System.getProperty("os.name").startsWith("Windows"))
            operatinSystem = OS.WINDOWS;

        if(System.getProperty("os.name").startsWith("lin"))
            operatinSystem = OS.LINUX;

        if(System.getProperty("os.name").startsWith("mac"))
            operatinSystem = OS.MAC;

        File injector = new File(folderPutInjectorAndStuff, "LunarClientLaunch" + (operatinSystem == OS.WINDOWS ? ".exe" : ""));
        injector.createNewFile();
        download("Nilsen84", "lunar-launcher-inject", injector, operatinSystem);

        File agent = new File(folderPutInjectorAndStuff, "WeaveAgent.jar");
        agent.createNewFile();

        download("Weave-MC", "Weave-Loader", agent, operatinSystem);

        System.exit(0);
        throw new RuntimeException("how tf");
    }

    public static void download(String owner, String repo, File to, OS os) throws Exception {
        boolean doOSCheck = true;

        if(owner.equals("Weave-MC"))
            doOSCheck = false;

        String apiUrl = String.format("https://api.github.com/repos/%s/%s/releases/latest", owner, repo);
        URL url = new URL(apiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String jsonString = in.readLine();
            String downloadUrl = "";

            if(!doOSCheck) {
                downloadUrl = jsonString.split("\"browser_download_url\":\"")[1].split("\"")[0];
            } else {
                List<String> downloadUrls = Arrays.stream(jsonString.split("\"browser_download_url\":\""))
                        .filter(s -> s.contains(os.name().toLowerCase()))
                        .map(s -> s.split("\"")[0])
                        .toList();

                if(!downloadUrls.isEmpty()) {
                    downloadUrl = downloadUrls.get(0);
                }
            }

            InputStream is = new URL(downloadUrl).openStream();
            FileOutputStream fout = new FileOutputStream(to);

            fout.write(is.readAllBytes());

            fout.close();
            is.close();
        }

    }

    enum OS {
        WINDOWS,
        LINUX,
        MAC
    }
}
