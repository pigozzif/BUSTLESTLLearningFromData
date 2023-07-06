package it.units.malelab.learningstl.roge;

import it.units.malelab.jgea.core.Individual;
import it.units.malelab.jgea.core.order.PartiallyOrderedCollection;
import it.units.malelab.jgea.core.selector.Selector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class Proportional implements Selector<Object> {

  public Proportional() {}

  public <K> K select(PartiallyOrderedCollection<K> ks, Random random) {
    double r = random.nextDouble();
    List<K> inds = new ArrayList<>(ks.all());
    double[] res = new double[inds.size()];
    for (int i = 1; i < res.length; i++) {
      res[i] = res[i - 1] + (double) ((Individual) inds.get(i - 1)).getFitness();
    }
    double[] finalRes = res;
    res = Arrays.stream(res).map(s -> s / finalRes[finalRes.length - 1]).toArray();
    for (int i = 0; i < res.length; i++) {
      if (res[i] > r) {
        return inds.get(i - 1);
      }
    }
    return inds.get(res.length - 1);
  }

}
