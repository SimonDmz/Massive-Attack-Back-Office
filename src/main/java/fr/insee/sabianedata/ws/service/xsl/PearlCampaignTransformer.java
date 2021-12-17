package fr.insee.sabianedata.ws.service.xsl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.sabianedata.ws.Constants;

public class PearlCampaignTransformer {

	private static XslTransformation saxonService = new XslTransformation();

	private static final Logger logger = LoggerFactory.getLogger(PearlCampaignTransformer.class);

	public File transform(File input, String fileName) throws Exception {

		File outputFile = new File(input.getParent(), fileName + ".xml");

		logger.debug("Output folder : " + outputFile.getAbsolutePath());

		InputStream inputStream = FileUtils.openInputStream(input);
		OutputStream outputStream = FileUtils.openOutputStream(outputFile);

		InputStream XSL = Constants.getInputStreamFromPath(Constants.PEARL_CAMPAIGN_FODS_TO_XML);
		try {
			saxonService.transformFods2XML(inputStream, outputStream, XSL);
		} catch (Exception e) {
			String errorMessage = "An error was occured during the campaign fods2xml transformation. " + e.getMessage();
			logger.error(errorMessage);
			throw new Exception(errorMessage);
		}

		inputStream.close();
		outputStream.close();
		XSL.close();
		logger.info("End of campaign transformation");

		return outputFile;
	}

	public File extractCampaign(File input) throws Exception {
		return extract(input, Constants.CAMPAIGN);
	}

	public File extractSurveyUnits(File input) throws Exception {
		return extract(input, Constants.SURVEY_UNITS);
	}

	public File extractAssignement(File input) throws Exception {
		return extract(input, Constants.ASSIGNEMENT);
	}

	public File extractInterviewers(File input) throws Exception {
		return extract(input, Constants.INTERVIEWERS);
	}

	public File extractContext(File input) throws Exception {
		return extract(input, Constants.CONTEXT);
	}

	public File extractGeoLocations(File input) throws Exception {
		return extract(input, Constants.GEO_LOCATIONS);
	}

	public File extract(File input, String type) throws Exception {
		File outputFile = new File(input.getParent(), type + ".xml");
		logger.debug("Output folder : " + outputFile.getAbsolutePath());

		InputStream inputStream = FileUtils.openInputStream(input);
		OutputStream outputStream = FileUtils.openOutputStream(outputFile);

		InputStream XSL = null;
		switch (type) {
		case Constants.CAMPAIGN:
			XSL = Constants.getInputStreamFromPath(Constants.PEARL_EXTRACT_CAMPAIGN);
			break;
		case Constants.ASSIGNEMENT:
			XSL = Constants.getInputStreamFromPath(Constants.PEARL_EXTRACT_ASSIGNEMENT);
			break;
		case Constants.SURVEY_UNITS:
			XSL = Constants.getInputStreamFromPath(Constants.PEARL_EXTRACT_SURVEYUNITS);
			break;
		case Constants.INTERVIEWERS:
			XSL = Constants.getInputStreamFromPath(Constants.PEARL_EXTRACT_INTERVIEWERS);
			break;
		case Constants.CONTEXT:
			XSL = Constants.getInputStreamFromPath(Constants.PEARL_EXTRACT_CONTEXT);
			break;
		case Constants.GEO_LOCATIONS:
			XSL = Constants.getInputStreamFromPath(Constants.GEOGRAHICAL_LOCATIONS_FODS_TO_XML);
		}
		try {
			saxonService.transformFods2XML(inputStream, outputStream, XSL);
		} catch (Exception e) {
			e.printStackTrace();
			String errorMessage = "An error was occured during the operations fods2xml transformation. "
					+ e.getMessage();
			logger.error(errorMessage);
			throw new Exception(errorMessage);
		}
		inputStream.close();
		outputStream.close();
		XSL.close();
		logger.info("End of extract pearl " + type);

		return outputFile;
	}
}
