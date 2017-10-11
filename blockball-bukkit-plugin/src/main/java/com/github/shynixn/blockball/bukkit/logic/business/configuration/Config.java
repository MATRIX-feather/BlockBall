package com.github.shynixn.blockball.bukkit.logic.business.configuration;

import java.util.List;

/**
 * Created by Shynixn 2017.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class Config extends SimpleConfig {
    private static final Config instance = new Config();

    /**
     * Returns the plugin prefix
     *
     * @return prefix
     */
    public String getPrefix() {
        return this.getData("messages.prefix");
    }

    /**
     * Returns the config instance.
     *
     * @return config
     */
    public static Config getInstance() {
        return instance;
    }

    /**
     * Returns if the stats scoreboard is enabled.
     *
     * @return isEnabled
     */
    public boolean isStatsScoreboardEnabled() {
        return this.getData("stats-scoreboard.enabled");
    }

    /**
     * Returns the stats scoreboard title.
     *
     * @return title
     */
    public String getStatsScoreboardTitle() {
        return this.getData("stats-scoreboard.title");
    }

    /**
     * Returns the stats scoreboard lines.
     *
     * @return lines
     */
    public List<String> getStatsScoreboardLines() {
        return this.getData("stats-scoreboard.lines");
    }
}
