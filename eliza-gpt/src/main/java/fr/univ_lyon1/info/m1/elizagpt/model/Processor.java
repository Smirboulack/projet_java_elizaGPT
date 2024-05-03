package fr.univ_lyon1.info.m1.elizagpt.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.opencsv.exceptions.CsvValidationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import fr.univ_lyon1.info.m1.elizagpt.model.responserules.IResponseRule;
import fr.univ_lyon1.info.m1.elizagpt.model.verb.VerbList;

/**
 * Logic to process a message (and probably reply to it).
 */
public class Processor {
    private String name;
    private final Random random = new Random();
    private VerbList verbList;
    private List<IResponseRule> responseRules;

    /**
     * Constructor.
     * 
     * @throws CsvValidationException l'exception
     */
    public Processor() {
        try {
            verbList = new VerbList();
            responseRules = loadRulesFromConfig(
                    "responseRules.properties");
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load all the response rules dynamically.
     * 
     * @param configFilePath the path to the config file
     * @return the list of rules
     * @throws FileNotFoundException l'exception
     */
    public List<IResponseRule> loadRulesFromConfig(final String configFilePath) {
        List<IResponseRule> rules = new ArrayList<>();
        Properties props = new Properties();

        // Load the config file from the classpath
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFilePath)) {
            if (input == null) {
                throw new FileNotFoundException(
                        "Fichier de configuration non trouvé dans le classpath");
            }

            props.load(input);
            String[] ruleClassNames = props.getProperty("rules").split(",");

            for (String className : ruleClassNames) {
                try {
                    // Ajoutez le package complet avant le nom de la classe
                    String fullClassName = "fr.univ_lyon1.info.m1.elizagpt.model.responserules."
                            + className.trim();
                    Class<?> clazz = Class.forName(fullClassName);
                    IResponseRule rule = (IResponseRule) clazz
                            .getDeclaredConstructor()
                            .newInstance();
                    rules.add(rule);
                } catch (ClassNotFoundException e) {
                    System.err.println("La classe " + className.trim() + " n'a pas été trouvée.");
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return rules;
    }

    /**
     * Normlize the text: remove extra spaces, add a final dot if missing.
     * 
     * @param text le texte à normalmiser
     * @return normalized text.
     */
    public String normalize(final String text) {
        return text.replaceAll("\\s+", " ")
                .replaceAll("^\\s+", "")
                .replaceAll("\\s+$", "")
                .replaceAll("[^\\.!?:]$", "$0.");
    }

    /**
     * Turn a 1st-person sentence (Je ...) into a plural 2nd person (Vous ...).
     * The result is not capitalized to allow forming a new sentence.
     *
     *
     * @param text le message à conjuger
     * @return The 2nd-person sentence.
     */
    public String firstToSecondPerson(final String text) throws CsvValidationException {
        String processedText = text;

        processedText = verbList.replacePronounsFirstToSecond(processedText);
        processedText = verbList.verbsFirstToSecond(processedText);
        processedText = verbList.replacePossessives(processedText);

        return processedText;
    }

    /**
     * Converts the given text from second-person to first-person,
     * including pronouns, verbs, and possessive pronouns.
     *
     * @param text The text to be converted.
     * @return The processed text with second-person replaced by first-person.
     */
    public String secondToFirstPerson(final String text) throws CsvValidationException {
        String processedText = text;

        processedText = verbList.replacePronounsSecondToFirst(processedText);
        processedText = verbList.verbsSecondToFirst(processedText);
        processedText = verbList.replacePossessives(processedText);

        return processedText;
    }

    /**
     * Generate a response to the input text.
     * 
     * @param input le message entré
     * @return The response (String).
     */
    public String generateResponse(final String input) {
        for (IResponseRule rule : responseRules) {
            if (rule.appliesTo(input, this)) {
                return rule.generateResponse(input, this);
            }
        }
        return null;
    }

    /**
     * Pick an element randomly in the array.
     * 
     * @param <T>   The type of the array
     * @param array The array to pick from
     * @return The randomly picked element
     */
    public <T> T pickRandom(final T[] array) {
        return array[random.nextInt(array.length)];
    }

    /**
     * return the name.
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     * 
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get random.
     * 
     * @return random
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Get for responseRules.
     * 
     * @return responseRules
     */
    public List<IResponseRule> getResponseRules() {
        return responseRules;
    }

    /**
     * Set the responseRules.
     * 
     * @param responseRules
     */
    public void setResponseRules(final List<IResponseRule> responseRules) {
        this.responseRules = responseRules;
    }

}
