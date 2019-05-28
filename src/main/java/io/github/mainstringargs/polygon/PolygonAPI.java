package io.github.mainstringargs.polygon;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import io.github.mainstringargs.alpaca.Utilities;
import io.github.mainstringargs.polygon.domain.DailyOpenClose;
import io.github.mainstringargs.polygon.domain.Quote;
import io.github.mainstringargs.polygon.domain.Snapshot;
import io.github.mainstringargs.polygon.domain.Trade;
import io.github.mainstringargs.polygon.domain.aggregate.Aggregates;
import io.github.mainstringargs.polygon.domain.historic.quotes.Quotes;
import io.github.mainstringargs.polygon.domain.historic.trades.Trades;
import io.github.mainstringargs.polygon.domain.meta.Exchange;
import io.github.mainstringargs.polygon.domain.meta.SymbolAnalystRatings;
import io.github.mainstringargs.polygon.domain.meta.SymbolDetails;
import io.github.mainstringargs.polygon.domain.meta.SymbolDividend;
import io.github.mainstringargs.polygon.domain.meta.SymbolEarning;
import io.github.mainstringargs.polygon.domain.meta.SymbolEndpoints;
import io.github.mainstringargs.polygon.domain.meta.SymbolFinancial;
import io.github.mainstringargs.polygon.domain.meta.SymbolNews;
import io.github.mainstringargs.polygon.domain.reference.Market;
import io.github.mainstringargs.polygon.domain.reference.Split;
import io.github.mainstringargs.polygon.domain.reference.Tickers;
import io.github.mainstringargs.polygon.domain.reference.TypesMapping;
import io.github.mainstringargs.polygon.enums.Locale;
import io.github.mainstringargs.polygon.enums.Sort;
import io.github.mainstringargs.polygon.enums.Timespan;
import io.github.mainstringargs.polygon.nats.PolygonNatsClient;
import io.github.mainstringargs.polygon.nats.PolygonStreamListener;
import io.github.mainstringargs.polygon.properties.PolygonProperties;
import io.github.mainstringargs.polygon.rest.PolygonRequest;
import io.github.mainstringargs.polygon.rest.PolygonRequestBuilder;
import io.github.mainstringargs.polygon.rest.exceptions.PolygonAPIException;

/**
 * The Class PolygonAPI.
 */
public class PolygonAPI {

  /** The logger. */
  private static Logger LOGGER = LogManager.getLogger(PolygonAPI.class);

  /** The polygon nats client. */
  private final PolygonNatsClient polygonNatsClient;

  /** The polygon request. */
  private final PolygonRequest polygonRequest;

  /** The base data url. */
  private String baseDataUrl;


  /**
   * Instantiates a new polygon API.
   */
  public PolygonAPI() {

    this(PolygonProperties.KEY_ID_VALUE);
  }

  /**
   * Instantiates a new polygon API.
   *
   * @param keyId the key id
   */
  public PolygonAPI(String keyId) {
    this(PolygonProperties.KEY_ID_VALUE, PolygonProperties.POLYGON_NATS_SERVERS_VALUE);

  }

  /**
   * Instantiates a new polygon API.
   *
   * @param keyId the key id
   * @param polygonNatsServers the polygon nats servers
   */
  public PolygonAPI(String keyId, String... polygonNatsServers) {

    LOGGER.info("PolygonAPI is using the following properties: \nkeyId: " + keyId
        + "\npolygonNatsServers " + Arrays.toString(polygonNatsServers));

    polygonRequest = new PolygonRequest(keyId);
    polygonNatsClient = new PolygonNatsClient(keyId, polygonNatsServers);
    baseDataUrl = PolygonProperties.BASE_DATA_URL_VALUE;

  }

  /**
   * Gets the symbol endpoints.
   *
   * @param symbol the symbol
   * @return the symbol endpoints
   * @throws PolygonAPIException the polygon API exception
   */
  public SymbolEndpoints getSymbolEndpoints(String symbol) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.META_ENDPOINT + "/" + PolygonConstants.SYMBOLS_ENDPOINT);

    builder.appendEndpoint(symbol);

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    SymbolEndpoints symbolEndpoints =
        polygonRequest.getResponseObject(response, SymbolEndpoints.class);

    return symbolEndpoints;
  }

  /**
   * Gets the symbol details.
   *
   * @param symbol the symbol
   * @return the symbol details
   * @throws PolygonAPIException the polygon API exception
   */
  public SymbolDetails getSymbolDetails(String symbol) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.META_ENDPOINT + "/" + PolygonConstants.SYMBOLS_ENDPOINT);

    builder.appendEndpoint(symbol);
    builder.appendEndpoint(PolygonConstants.COMPANY_ENDPOINT);

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);


    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    SymbolDetails symbolDetails = polygonRequest.getResponseObject(response, SymbolDetails.class);

    return symbolDetails;
  }

  /**
   * Gets the symbol analyst ratings.
   *
   * @param symbol the symbol
   * @return the symbol analyst ratings
   * @throws PolygonAPIException the polygon API exception
   */
  public SymbolAnalystRatings getSymbolAnalystRatings(String symbol) throws PolygonAPIException {


    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.META_ENDPOINT + "/" + PolygonConstants.SYMBOLS_ENDPOINT);

    builder.appendEndpoint(symbol);
    builder.appendEndpoint(PolygonConstants.ANALYSTS_ENDPOINT);


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    SymbolAnalystRatings symbolDetails =
        polygonRequest.getResponseObject(response, SymbolAnalystRatings.class);

    return symbolDetails;
  }

  /**
   * Gets the symbol dividends.
   *
   * @param symbol the symbol
   * @return the symbol dividends
   * @throws PolygonAPIException the polygon API exception
   */
  public List<SymbolDividend> getSymbolDividends(String symbol) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.META_ENDPOINT + "/" + PolygonConstants.SYMBOLS_ENDPOINT);

    builder.appendEndpoint(symbol);
    builder.appendEndpoint(PolygonConstants.DIVIDENDS_ENDPOINT);


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<SymbolDividend>>() {}.getType();

    List<SymbolDividend> symbolDetails = polygonRequest.getResponseObject(response, listType);

    return symbolDetails;
  }

  /**
   * Gets the symbol earnings.
   *
   * @param symbol the symbol
   * @return the symbol earnings
   * @throws PolygonAPIException the polygon API exception
   */
  public List<SymbolEarning> getSymbolEarnings(String symbol) throws PolygonAPIException {


    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.META_ENDPOINT + "/" + PolygonConstants.SYMBOLS_ENDPOINT);

    builder.appendEndpoint(symbol);
    builder.appendEndpoint(PolygonConstants.EARNINGS_ENDPOINT);


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<SymbolEarning>>() {}.getType();

    List<SymbolEarning> symbolDetails = polygonRequest.getResponseObject(response, listType);

    return symbolDetails;
  }


  /**
   * Gets the symbol financials.
   *
   * @param symbol the symbol
   * @return the symbol financials
   * @throws PolygonAPIException the polygon API exception
   */
  public List<SymbolFinancial> getSymbolFinancials(String symbol) throws PolygonAPIException {


    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.META_ENDPOINT + "/" + PolygonConstants.SYMBOLS_ENDPOINT);

    builder.appendEndpoint(symbol);
    builder.appendEndpoint(PolygonConstants.FINANCIALS_ENDPOINT);


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<SymbolFinancial>>() {}.getType();

    List<SymbolFinancial> symbolDetails = polygonRequest.getResponseObject(response, listType);

    return symbolDetails;
  }

  /**
   * Gets the symbol news.
   *
   * @param symbol the symbol
   * @return the symbol news
   * @throws PolygonAPIException the polygon API exception
   */
  public List<SymbolNews> getSymbolNews(String symbol) throws PolygonAPIException {


    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.META_ENDPOINT + "/" + PolygonConstants.SYMBOLS_ENDPOINT);

    builder.appendEndpoint(symbol);
    builder.appendEndpoint(PolygonConstants.NEWS_ENDPOINT);


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<SymbolNews>>() {}.getType();


    List<SymbolNews> symbolDetails = polygonRequest.getResponseObject(response, listType);

    return symbolDetails;
  }

  /**
   * Gets the symbol news.
   *
   * @param symbol the symbol
   * @param perpage the perpage
   * @param page the page
   * @return the symbol news
   * @throws PolygonAPIException the polygon API exception
   */
  public List<SymbolNews> getSymbolNews(String symbol, Integer perpage, Integer page)
      throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.META_ENDPOINT + "/" + PolygonConstants.SYMBOLS_ENDPOINT);

    builder.appendEndpoint(symbol);
    builder.appendEndpoint(PolygonConstants.NEWS_ENDPOINT);
    if (perpage != null) {
      builder.appendURLParameter(PolygonConstants.PERPAGE_PARAMETER, perpage + "");
    }

    if (page != null) {
      builder.appendURLParameter(PolygonConstants.PAGE_PARAMETER, page + "");
    }

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<SymbolNews>>() {}.getType();


    List<SymbolNews> symbolDetails = polygonRequest.getResponseObject(response, listType);

    return symbolDetails;
  }

  /**
   * Gets the tickers.
   *
   * @param sort the sort
   * @param type the type
   * @param market the market
   * @param locale the locale
   * @param search the search
   * @param perpage the perpage
   * @param page the page
   * @param active the active
   * @return the tickers
   * @throws PolygonAPIException the polygon API exception
   */
  public Tickers getTickers(Sort sort, io.github.mainstringargs.polygon.enums.Type type,
      io.github.mainstringargs.polygon.enums.Market market, Locale locale, String search,
      Integer perpage, Integer page, Boolean active) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.REFERENCE_ENDPOINT + "/" + PolygonConstants.TICKERS_ENDPOINT);

    builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);

    if (sort != null) {
      builder.appendURLParameter(PolygonConstants.SORT_PARAMETER, sort.getAPIName());
    }

    if (type != null) {
      builder.appendURLParameter(PolygonConstants.TYPE_PARAMETER, type.getAPIName());
    }

    if (market != null) {
      builder.appendURLParameter(PolygonConstants.MARKET_PARAMETER, market.getAPIName());
    }

    if (locale != null) {
      builder.appendURLParameter(PolygonConstants.LOCALE_PARAMETER, locale.getAPIName());
    }

    if (search != null) {
      builder.appendURLParameter(PolygonConstants.SEARCH_PARAMETER, search);
    }

    if (perpage != null) {
      builder.appendURLParameter(PolygonConstants.PERPAGE_PARAMETER, perpage + "");
    }

    if (page != null) {
      builder.appendURLParameter(PolygonConstants.PAGE_PARAMETER, page + "");
    }

    if (active != null) {
      builder.appendURLParameter(PolygonConstants.ACTIVE_PARAMETER, active + "");
    }

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Tickers symbolDetails = polygonRequest.getResponseObject(response, Tickers.class);

    return symbolDetails;
  }

  /**
   * Gets the markets.
   *
   * @return the markets
   * @throws PolygonAPIException the polygon API exception
   */
  public List<Market> getMarkets() throws PolygonAPIException {


    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.REFERENCE_ENDPOINT + "/" + PolygonConstants.MARKETS_ENDPOINT);

    builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }


    Type listType = new TypeToken<List<Market>>() {}.getType();

    List<Market> markets = polygonRequest.getResponseObject(response, listType);

    return markets;
  }

  /**
   * Gets the locales.
   *
   * @return the locales
   * @throws PolygonAPIException the polygon API exception
   */
  public List<io.github.mainstringargs.polygon.domain.reference.Locale> getLocales()
      throws PolygonAPIException {


    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.REFERENCE_ENDPOINT + "/" + PolygonConstants.LOCALES_ENDPOINT);

    builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }


    Type listType =
        new TypeToken<List<io.github.mainstringargs.polygon.domain.reference.Locale>>() {}
            .getType();

    List<io.github.mainstringargs.polygon.domain.reference.Locale> locales =
        polygonRequest.getResponseObject(response, listType);

    return locales;
  }

  /**
   * Gets the types mapping.
   *
   * @return the types mapping
   * @throws PolygonAPIException the polygon API exception
   */
  public TypesMapping getTypesMapping() throws PolygonAPIException {


    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.REFERENCE_ENDPOINT + "/" + PolygonConstants.TYPES_ENDPOINT);

    builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }


    TypesMapping typesMapping = polygonRequest.getResponseObject(response, TypesMapping.class);

    return typesMapping;
  }

  /**
   * Gets the splits.
   *
   * @param symbol the symbol
   * @return the splits
   * @throws PolygonAPIException the polygon API exception
   */
  public List<Split> getSplits(String symbol) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.REFERENCE_ENDPOINT + "/" + PolygonConstants.SPLITS_ENDPOINT);

    builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);

    builder.appendEndpoint(symbol);


    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<Split>>() {}.getType();


    List<Split> splits = polygonRequest.getResponseObject(response, listType);

    return splits;
  }

  /**
   * Gets the exchange.
   *
   * @return the exchange
   * @throws PolygonAPIException the polygon API exception
   */
  public List<Exchange> getExchanges() throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.META_ENDPOINT + "/" + PolygonConstants.EXCHANGES_ENDPOINT);

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<Exchange>>() {}.getType();


    List<Exchange> exchanges = polygonRequest.getResponseObject(response, listType);

    return exchanges;
  }

  /**
   * Gets the historic trades.
   *
   * @param symbol the symbol
   * @param date the date
   * @param offset the offset
   * @param limit the limit
   * @return the historic trades
   * @throws PolygonAPIException the polygon API exception
   */
  public Trades getHistoricTrades(String symbol, LocalDate date, Integer offset, Integer limit)
      throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.HISTORIC_ENDPOINT + "/" + PolygonConstants.TRADES_ENDPOINT);

    builder.appendEndpoint(symbol);
    builder.appendEndpoint(Utilities.toDateString(date));
    if (offset != null) {
      builder.appendURLParameter("offset", offset + "");
    }

    if (limit != null) {
      builder.appendURLParameter("limit", limit + "");
    }

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Trades tradesDetails = polygonRequest.getResponseObject(response, Trades.class);

    return tradesDetails;
  }

  /**
   * Gets the historic quotes.
   *
   * @param symbol the symbol
   * @param date the date
   * @param offset the offset
   * @param limit the limit
   * @return the historic quotes
   * @throws PolygonAPIException the polygon API exception
   */
  public Quotes getHistoricQuotes(String symbol, LocalDate date, Integer offset, Integer limit)
      throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.HISTORIC_ENDPOINT + "/" + PolygonConstants.QUOTES_ENDPOINT);

    builder.appendEndpoint(symbol);
    builder.appendEndpoint(Utilities.toDateString(date));
    if (offset != null) {
      builder.appendURLParameter("offset", offset + "");
    }

    if (limit != null) {
      builder.appendURLParameter("limit", limit + "");
    }

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Quotes quotesDetails = polygonRequest.getResponseObject(response, Quotes.class);

    return quotesDetails;
  }

  /**
   * Gets the last trade.
   *
   * @param symbol the symbol
   * @return the last trade
   * @throws PolygonAPIException the polygon API exception
   */
  public Trade getLastTrade(String symbol) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.LAST_ENDPOINT + "/" + PolygonConstants.STOCKS_ENDPOINT);

    builder.appendEndpoint(symbol);

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Trade stockTrade = polygonRequest.getResponseObject(response, Trade.class);

    return stockTrade;
  }

  /**
   * Gets the last quote.
   *
   * @param symbol the symbol
   * @return the last quote
   * @throws PolygonAPIException the polygon API exception
   */
  public Quote getLastQuote(String symbol) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.LAST_QUOTE_ENDPOINT + "/" + PolygonConstants.STOCKS_ENDPOINT);

    builder.appendEndpoint(symbol);

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Quote stockQuote = polygonRequest.getResponseObject(response, Quote.class);

    return stockQuote;
  }

  /**
   * Gets the daily open close.
   *
   * @param symbol the symbol
   * @param date the date
   * @return the daily open close
   * @throws PolygonAPIException the polygon API exception
   */
  public DailyOpenClose getDailyOpenClose(String symbol, LocalDate date)
      throws PolygonAPIException {

    PolygonRequestBuilder builder =
        new PolygonRequestBuilder(baseDataUrl, PolygonConstants.OPEN_CLOSE_ENDPOINT);

    builder.appendEndpoint(symbol);
    builder.appendEndpoint(Utilities.toDateString(date));

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    DailyOpenClose dailyOpenClose =
        polygonRequest.getResponseObject(response, DailyOpenClose.class);

    return dailyOpenClose;
  }

  /**
   * Gets the snapshot all tickers.
   *
   * @return the snapshot all tickers
   * @throws PolygonAPIException the polygon API exception
   */
  public List<Snapshot> getSnapshotAllTickers() throws PolygonAPIException {

    PolygonRequestBuilder builder =
        new PolygonRequestBuilder(baseDataUrl, "snapshot/locale/us/markets/stocks/tickers");

    builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<Snapshot>>() {}.getType();

    List<Snapshot> snapshots = polygonRequest.getResponseObject(response, listType);

    return snapshots;
  }

  /**
   * Gets the snapshot.
   *
   * @param symbol the symbol
   * @return the snapshot
   * @throws PolygonAPIException the polygon API exception
   */
  public Snapshot getSnapshot(String symbol) throws PolygonAPIException {

    PolygonRequestBuilder builder =
        new PolygonRequestBuilder(baseDataUrl, "snapshot/locale/us/markets/stocks/tickers");

    builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);
    builder.appendEndpoint(symbol);

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }


    Snapshot snapshot = polygonRequest.getResponseObject(response, Snapshot.class);

    return snapshot;
  }

  /**
   * Gets the snapshots gainers.
   *
   * @return the snapshots gainers
   * @throws PolygonAPIException the polygon API exception
   */
  public List<Snapshot> getSnapshotsGainers() throws PolygonAPIException {

    PolygonRequestBuilder builder =
        new PolygonRequestBuilder(baseDataUrl, "snapshot/locale/us/markets/stocks/gainers");

    builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<Snapshot>>() {}.getType();

    List<Snapshot> snapshots = polygonRequest.getResponseObject(response, listType);

    return snapshots;
  }

  /**
   * Gets the snapshots losers.
   *
   * @return the snapshots losers
   * @throws PolygonAPIException the polygon API exception
   */
  public List<Snapshot> getSnapshotsLosers() throws PolygonAPIException {

    PolygonRequestBuilder builder =
        new PolygonRequestBuilder(baseDataUrl, "snapshot/locale/us/markets/stocks/losers");

    builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }

    Type listType = new TypeToken<List<Snapshot>>() {}.getType();

    List<Snapshot> snapshots = polygonRequest.getResponseObject(response, listType);

    return snapshots;
  }

  /**
   * Gets the previous close.
   *
   * @param ticker the ticker
   * @param unadjusted the unadjusted
   * @return the previous close
   * @throws PolygonAPIException the polygon API exception
   */
  public Aggregates getPreviousClose(String ticker, Boolean unadjusted) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.AGGS_ENDPOINT + "/" + PolygonConstants.TICKER_ENDPOINT);

    builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);
    builder.appendEndpoint(ticker);
    builder.appendEndpoint("prev");

    if (unadjusted != null) {
      builder.appendURLParameter("unadjusted", unadjusted.toString());
    }

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }


    Aggregates aggregates = polygonRequest.getResponseObject(response, Aggregates.class);

    if (aggregates.getResultsCount() == 1) {
      aggregates.getResults().get(0).setTicker(ticker);
    }

    return aggregates;
  }

  /**
   * Gets the aggregates.
   *
   * @param ticker the ticker
   * @param multiplier the multiplier
   * @param timeSpan the time span
   * @param fromDate the from date
   * @param toDate the to date
   * @param unadjusted the unadjusted
   * @return the aggregates
   * @throws PolygonAPIException the polygon API exception
   */
  public Aggregates getAggregates(String ticker, Integer multiplier, Timespan timeSpan,
      LocalDate fromDate, LocalDate toDate, Boolean unadjusted) throws PolygonAPIException {

    PolygonRequestBuilder builder = new PolygonRequestBuilder(baseDataUrl,
        PolygonConstants.AGGS_ENDPOINT + "/" + PolygonConstants.TICKER_ENDPOINT);

    builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);
    builder.appendEndpoint(ticker);
    builder.appendEndpoint("range");
    builder.appendEndpoint(Integer.toString((multiplier != null) ? multiplier : 1));
    builder.appendEndpoint(timeSpan.getAPIName());
    builder.appendEndpoint(Utilities.toDateString(fromDate));
    builder.appendEndpoint(Utilities.toDateString(toDate));

    if (unadjusted != null) {
      builder.appendURLParameter("unadjusted", unadjusted.toString());
    }

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }


    Aggregates aggregates = polygonRequest.getResponseObject(response, Aggregates.class);

    if (aggregates.getResultsCount() != 0) {
      List<io.github.mainstringargs.polygon.domain.aggregate.Result> results =
          aggregates.getResults();
      for (io.github.mainstringargs.polygon.domain.aggregate.Result result : results) {
        result.setTicker(ticker);
      }
    }

    return aggregates;
  }

  /**
   * Gets the grouped daily.
   *
   * @param locale the locale
   * @param market the market
   * @param date the date
   * @param unadjusted the unadjusted
   * @return the grouped daily
   * @throws PolygonAPIException the polygon API exception
   */
  public Aggregates getGroupedDaily(Locale locale,
      io.github.mainstringargs.polygon.enums.Market market, LocalDate date, Boolean unadjusted)
      throws PolygonAPIException {

    PolygonRequestBuilder builder =
        new PolygonRequestBuilder(baseDataUrl, PolygonConstants.AGGS_ENDPOINT + "/"
            + PolygonConstants.GROUPED_ENDPOINT + "/" + PolygonConstants.LOCALE_ENDPOINT);

    builder.setVersion(PolygonConstants.VERSION_2_ENDPOINT);
    builder.appendEndpoint(locale.getAPIName());
    builder.appendEndpoint(PolygonConstants.MARKET_ENDPOINT);
    builder.appendEndpoint(market.getAPIName());
    builder.appendEndpoint(Utilities.toDateString(date));

    if (unadjusted != null) {
      builder.appendURLParameter("unadjusted", unadjusted.toString());
    }

    HttpResponse<JsonNode> response = polygonRequest.invokeGet(builder);

    if (response.getStatus() != 200) {
      throw new PolygonAPIException(response);
    }


    Aggregates aggregates = polygonRequest.getResponseObject(response, Aggregates.class);

    if (aggregates.getResultsCount() != 0) {
      List<io.github.mainstringargs.polygon.domain.aggregate.Result> results =
          aggregates.getResults();
      JsonNode responseJson = response.getBody();
      JSONArray resultsArr = (JSONArray) responseJson.getObject().get("results");

      for (int i = 0; i < results.size(); i++) {
        io.github.mainstringargs.polygon.domain.aggregate.Result result = results.get(i);
        JSONObject jsonObj = (JSONObject) resultsArr.get(i);
        result.setTicker(jsonObj.get("T").toString());


      }
    }

    return aggregates;
  }

  /**
   * Adds the polygon stream listener.
   *
   * @param streamListener the stream listener
   */
  public void addPolygonStreamListener(PolygonStreamListener streamListener) {
    polygonNatsClient.addListener(streamListener);
  }


  /**
   * Removes the polygon stream listener.
   *
   * @param streamListener the stream listener
   */
  public void removePolygonStreamListener(PolygonStreamListener streamListener) {
    polygonNatsClient.removeListener(streamListener);
  }

}
