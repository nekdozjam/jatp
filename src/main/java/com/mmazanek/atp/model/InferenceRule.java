package com.mmazanek.atp.model;

import java.util.List;

public abstract class InferenceRule {
 public abstract List<ClauseEntry> apply(List<ClauseEntry> active, List<ClauseEntry> waiting, ClauseEntry current, ClauseEntry second);
}
