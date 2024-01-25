In order to instanciate the restaurants, we used the API of Foursquare. We used the following
endpoint http://webservices-v2.crous-mobile.fr/feed/toulouse/externe/resto.xml, which returns an XML
of CROUS restaurants of Toulouse. And to parse it we used the following code :

```
ExecutorService executorService = Executors.newFixedThreadPool(1);
Future<String> future = executorService.submit(() -> downloadXml("http://webservices-v2.crous-mobile.fr/feed/toulouse/externe/resto.xml"));
try {
    String result = future.get();
    if (result != null) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream is = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
        Document document = builder.parse(is);
        Element rootElement = document.getDocumentElement();
        NodeList restoList = rootElement.getElementsByTagName("resto");
        List<Restaurant> restaurantList = new ArrayList<>();
        for (int i = 0; i < restoList.getLength(); i++) {
            Element restoElement = (Element) restoList.item(i);
            String id = restoElement.getAttribute("id");
            String title = restoElement.getAttribute("title");
            String openingHours = restoElement.getAttribute("opening");
            String closingTime = restoElement.getAttribute("closing");
            String shortDesc = restoElement.getAttribute("short_desc");
            String lat = restoElement.getAttribute("lat");
            String lon = restoElement.getAttribute("lon");
            String zone = restoElement.getAttribute("zone");
            NodeList infosList = restoElement.getElementsByTagName("infos");
            String infos = infosList.item(0).getTextContent();
            NodeList contactList = restoElement.getElementsByTagName("contact");
            String contact = contactList.item(0).getTextContent();
            NodeList crousAndGoList = restoElement.getElementsByTagName("crousandgo");
            String crousAndGoSrc = crousAndGoList.item(0).getAttributes().getNamedItem("src").getTextContent();
            Restaurant restaurant = new Restaurant(id, title, openingHours, closingTime,
                    shortDesc, lat, lon, zone, infos, contact, crousAndGoSrc);
            restaurantList.add(restaurant);
        }
        for (Restaurant restaurant : restaurantList) {
            FirebaseManager.getInstance().saveRestaurants(restaurantList);
            System.out.println(restaurant);
        }
    } else {
        System.err.println("Error downloading XML");
    }
} catch (Exception e) {
    e.printStackTrace();
} finally {
    executorService.shutdown();
}
```

```
private String downloadXml(String urlString) {
    try {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        InputStream in = urlConnection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        in.close();
        return stringBuilder.toString();
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
```