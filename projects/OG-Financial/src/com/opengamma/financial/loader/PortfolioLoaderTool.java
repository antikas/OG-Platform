/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.loader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.financial.loader.portfolio.DummyPortfolioWriter;
import com.opengamma.financial.loader.portfolio.MasterPortfolioWriter;
import com.opengamma.financial.loader.portfolio.PortfolioReader;
import com.opengamma.financial.loader.portfolio.PortfolioWriter;
import com.opengamma.financial.loader.portfolio.SingleSheetSimplePortfolioReader;
import com.opengamma.financial.loader.portfolio.ZippedPortfolioReader;
import com.opengamma.financial.tool.ToolContext;

/**
 * Provides standard portfolio loader functionality
 */
public class PortfolioLoaderTool {

  private static final Logger s_logger = LoggerFactory.getLogger(PortfolioLoaderTool.class);

  /** Tool name */
  private static final String TOOL_NAME = "OpenGamma Portfolio Importer";
  /** File name option flag */
  private static final String FILE_NAME_OPT = "f";
  /** Portfolio name option flag*/
  private static final String PORTFOLIO_NAME_OPT = "n";
  /** Write option flag */
  private static final String WRITE_OPT = "w";
  /** Asset class flag */
  private static final String ASSET_CLASS_OPT = "a";

  /**
   * ENTRY POINT FOR COMMAND LINE TOOL
   * @param args  Command line args
   * @param toolContext  the loader context
   */
  public void run(String[] args, ToolContext toolContext) {
    s_logger.info(TOOL_NAME + " is initialising...");
    s_logger.info("Current working directory is " + System.getProperty("user.dir"));
    
    // Parse command line arguments
    CommandLine cmdLine = getCmdLine(args, true);
    
    run(cmdLine, toolContext);
  }

  private void run(CommandLine cmdLine, ToolContext toolContext) {
    // Set up writer
    PortfolioWriter portfolioWriter = constructPortfolioWriter(
        cmdLine.getOptionValue(PORTFOLIO_NAME_OPT), 
        cmdLine.hasOption(WRITE_OPT),
        toolContext);
    
     // Set up reader
    PortfolioReader portfolioReader = constructPortfolioReader(
        cmdLine.getOptionValue(FILE_NAME_OPT), 
        cmdLine.getOptionValue(ASSET_CLASS_OPT), 
        toolContext);
    
    // Load in and write the securities, positions and trades
    portfolioReader.writeTo(portfolioWriter);
    
    // Flush changes to portfolio master & close
    portfolioWriter.flush();
    portfolioWriter.close();
    
    s_logger.info(TOOL_NAME + " is finished.");
  }
  
  
  private static CommandLine getCmdLine(String[] args, boolean contextProvided) {
    final Options options = getOptions(contextProvided);
    try {
      return new PosixParser().parse(options, args);
    } catch (ParseException e) {
      s_logger.warn(e.getMessage());
      (new HelpFormatter()).printHelp(" ", options);
      throw new OpenGammaRuntimeException("Could not parse the command line");
    }        
  }

  private static Options getOptions(boolean contextProvided) {
    Options options = new Options();
    Option filenameOption = new Option(
        FILE_NAME_OPT, "filename", true, "The path to the file containing data to import (CSV or ZIP)");
    filenameOption.setRequired(true);
    options.addOption(filenameOption);
    
    Option portfolioNameOption = new Option(
        PORTFOLIO_NAME_OPT, "name", true, "The name of the destination OpenGamma portfolio");
    options.addOption(portfolioNameOption);
    
    Option writeOption = new Option(
        WRITE_OPT, "write", false, 
        "Actually persists the portfolio to the database if specified, otherwise pretty-prints without persisting");
    options.addOption(writeOption);
    
    Option assetClassOption = new Option(
        ASSET_CLASS_OPT, "assetclass", true, 
        "Specifies the asset class to be found in an input CSV file (ignored if ZIP file is specified)");
    options.addOption(assetClassOption);
    
    return options;
  }

  private static PortfolioWriter constructPortfolioWriter(String portfolioName, boolean write, ToolContext toolContext) {
    if (write) {  
      // Check that the portfolio name was specified on the command line
      if (portfolioName == null) {
        throw new OpenGammaRuntimeException("Portfolio name omitted, cannot persist to OpenGamma masters");
      }
      
      s_logger.info("Write option specified, will persist to OpenGamma masters in portfolio '" + portfolioName + "'");
      
      // Create a portfolio writer to persist imported positions, trades and securities to the OG masters
      return new MasterPortfolioWriter(portfolioName, toolContext);
      
    } else {
      s_logger.info("Write option omitted, will pretty-print instead of persisting to OpenGamma masters");
      
      // Create a dummy portfolio writer to pretty-print instead of persisting
      return new DummyPortfolioWriter();         
    }

  }

  private static PortfolioReader constructPortfolioReader(String filename, String securityClass, ToolContext toolContext) {
    String extension = filename.substring(filename.lastIndexOf('.'));
    
    // Single CSV or XLS file extension
    if (extension.equalsIgnoreCase(".csv") || extension.equalsIgnoreCase(".xls")) {
      // Check that the asset class was specified on the command line
      if (securityClass == null) {
        throw new OpenGammaRuntimeException("Could not import as no asset class was specified for file " + filename + " (use '-a')");
      } else {
        return new SingleSheetSimplePortfolioReader(filename, securityClass, toolContext);
      }
    // Multi-asset ZIP file extension
    } else if (extension.equalsIgnoreCase(".zip")) {
      // Create zipped multi-asset class loader
      return new ZippedPortfolioReader(filename, toolContext);
    } else {
      throw new OpenGammaRuntimeException("Input filename should end in .CSV, .XLS or .ZIP");
    }
  }

}
