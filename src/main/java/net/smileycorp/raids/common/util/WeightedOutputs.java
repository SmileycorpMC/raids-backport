package net.smileycorp.raids.common.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;

public class WeightedOutputs<T> {

	protected final List<Entry<T, Integer>> entries = new ArrayList<Entry<T, Integer>>();
	protected int defaultTries;

	public WeightedOutputs(Map<T, Integer> entries) {
		this(1, new ArrayList<Entry<T, Integer>>(entries.entrySet()));
	}

	public WeightedOutputs(int defaultTries, Map<T, Integer> entries) {
		this(defaultTries, entries.entrySet());
	}

	public WeightedOutputs(int defaultTries, Collection<Entry<T, Integer>> entries) {
		this.defaultTries = defaultTries;
		this.entries.addAll(entries);
	}

	public int getAmount() {
		return defaultTries;
	}

	public List<Entry<T, Integer>> getTable() {
		return entries;
	}

	public boolean isEmpty() {
		return entries.isEmpty();
	}

	public T getResult(Random rand) {
		return getResults(rand, 1).get(0);
	}

	public List<T> getResults(Random rand) {
		return getResults(rand, 1);
	}

	public void clear() {
		entries.clear();
	}

	public int getWeight(T t) {
		for (Entry<T, Integer> entry : entries) {
			if (entry.getKey() == t) entry.getValue();
		}
		return 0;
	}

	public boolean removeEntry(T t) {
		boolean result = false;
		for (Entry<T, Integer> entry : entries) {
			if (entry.getKey() == t) {
				entries.remove(entry);
				result = true;
			}
		}
		return result;
	}

	public void setDefaultTries(int value) {
		defaultTries = value;
	}

	public void addEntry(T t, int weight) {
		entries.add(new SimpleEntry<T, Integer>(t, weight));
	}

	public void addEntries(Collection<Entry<T, Integer>> entries) {
		this.entries.addAll(entries);
	}

	public void addEntries(Map<T, Integer> entries) {
		this.entries.addAll(entries.entrySet());
	}

	public List<T> getResults(Random rand, int tries) {
		List<T> list = new ArrayList<T>();
		List<Entry<T, Integer>> mappedEntries = new ArrayList<Entry<T, Integer>>();
		int max = 0;
		for(Entry<T, Integer> entry : entries) {
			mappedEntries.add(new SimpleEntry<T, Integer>(entry.getKey(), max));
			max+=entry.getValue();
		}
		if (max>0) {
			Collections.reverse(mappedEntries);
			for(int i=0; i<(tries*defaultTries);i++){
				int result = rand.nextInt(max);
				for(Entry<T, Integer> entry : mappedEntries) {
					if (result>=entry.getValue()) {
						list.add(entry.getKey());
						break;
					}
				}
			}
		}
		return list;
	}
}
