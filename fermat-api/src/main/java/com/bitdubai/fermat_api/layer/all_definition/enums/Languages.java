package com.bitdubai.fermat_api.layer.all_definition.enums;

import com.bitdubai.fermat_api.layer.all_definition.enums.interfaces.FermatEnum;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;

/**
 * The enum class <code>com.bitdubai.fermat_api.layer.all_definition.enums.Languages</code>
 * enumerates all the Language types of fermat.
 * <p/>
 * Created by ciencias on 2/14/15.
 * Modified by Manuel Perez on 03/08/2015.
 * Updated by PatricioGesualdi - (pmgesualdi@hotmail.com) on 18/11/2015.
 */
public enum Languages implements FermatEnum {
    /**
     * For doing the code more readable, please keep the elements in the Enum sorted alphabetically.
     */
    AMERICAN_ENGLISH        ("AMERICAN_ENGLISH"),
    ENGLISH                 ("ENGLISH"),
    GREAT_BRITAIN_ENGLISH   ("GREAT_BRITAIN_ENGLISH"),
    LATIN_AMERICAN_SPANISH  ("LATIN_AMERICAN_SPANISH"),
    SPANISH                 ("SPANISH")

    ;

    private final String code;

    Languages(final String code) { this.code = code; }

    public static Languages fromValue(String code) {

        switch (code) {

            case "AMERICAN_ENGLISH":       return Languages.AMERICAN_ENGLISH;
            case "ENGLISH":                return Languages.ENGLISH;
            case "GREAT_BRITAIN_ENGLISH":  return Languages.GREAT_BRITAIN_ENGLISH;
            case "LATIN_AMERICAN_SPANISH": return Languages.LATIN_AMERICAN_SPANISH;
            case "SPANISH":                return Languages.SPANISH;

            default:
                return Languages.ENGLISH;
        }
    }

    @Override
    public String getCode() { return code; }
}
