package com.carver.paul.dotavision.Models;

/**
 * Created by paul on 15/10/16.
 */

import java.util.List;

/**
 * Interface for the presenters which will show the information about hero abilities.
 */
public interface IInfoPresenter {
    void reset();
    void prepareForFreshList();
    void showHeroInfo(List<HeroInfo> enemyHeroes, List<HeroAndAdvantages> advantageData);
}
