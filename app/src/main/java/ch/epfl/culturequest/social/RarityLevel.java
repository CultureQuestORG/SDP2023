package ch.epfl.culturequest.social;

import ch.epfl.culturequest.R;

public enum RarityLevel {
    COMMON, ORIGINAL, RARE, EPIC;


    /**
     * Returns the icon corresponding to the rarity level
     * @return (int) the resId of the icon corresponding to the rarity level
     */
    public int getRarenessIcon() {
        switch (this) {
            case COMMON:
                return R.drawable.common;
            case ORIGINAL:
                return R.drawable.original;
            case RARE:
                return R.drawable.rare;
            case EPIC:
                return R.drawable.epic;
            default:
                return R.drawable.common;
        }
    }

    /**
     * Returns the RarityLevel corresponding to the given score
     * @param score the score of the art (between 0 and 100)
     * @return the RarityLevel corresponding to the given score
     */
    public static RarityLevel getRarityLevel(int score) {
        if (score < 0 || score > 100) throw new IllegalArgumentException("Score must be between 0 and 100");
        if (score < 40) {
            return COMMON;
        } else if (score < 70) {
            return ORIGINAL;
        } else if (score < 90) {
            return RARE;
        } else {
            return EPIC;
        }
    }
}
