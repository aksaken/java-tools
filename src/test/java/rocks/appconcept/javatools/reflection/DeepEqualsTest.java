package rocks.appconcept.javatools.reflection;

import static java.lang.Math.E;
import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;
import org.junit.Test;

/**
 * Created by imeta on 29-Jan-17.
 */
public class DeepEqualsTest {

  @Test
  public void testSameObjectEquals() {
    Date date1 = new Date();
    assertTrue(DeepEquals.deepEquals(date1, date1));
  }

  @Test
  public void testEqualsWithNull() {
    Date date1 = new Date();
    assertFalse(DeepEquals.deepEquals(null, date1));
    assertFalse(DeepEquals.deepEquals(date1, null));
  }

  @Test
  public void testDifferentClasses() {
    assertFalse(DeepEquals.deepEquals(new Date(), "test"));
  }

  @Test
  public void testPOJOequals() {
    Class1 x = new Class1(true, tan(PI / 4), 1);
    Class1 y = new Class1(true, 1.0, 1);
    assertTrue(DeepEquals.deepEquals(x, y));
    assertFalse(DeepEquals.deepEquals(x, new Class1()));

    Class2 a = new Class2((float) atan(1.0), "hello", (short) 2,
        new Class1(false, sin(0.75), 5));
    Class2 b = new Class2((float) PI / 4, "hello", (short) 2,
        new Class1(false, 2 * cos(0.75 / 2) * sin(0.75 / 2), 5)
    );

    assertTrue(DeepEquals.deepEquals(a, b));
    assertFalse(DeepEquals.deepEquals(a, new Class2()));
  }

  @Test
  public void testPrimitiveArrays() {
    int array1[] = {2, 4, 5, 6, 3, 1, 3, 3, 5, 22};
    int array2[] = {2, 4, 5, 6, 3, 1, 3, 3, 5, 22};

    assertTrue(DeepEquals.deepEquals(array1, array2));

    int array3[] = {3, 4, 7};

    assertFalse(DeepEquals.deepEquals(array1, array3));

    float array4[] = {3.4f, 5.5f};
    assertFalse(DeepEquals.deepEquals(array1, array4));
  }

  @Test
  public void testOrderedCollection() {
    List<String> a = new ArrayList<>();
    a.add("one");
    a.add("two");
    a.add("three");
    a.add("four");
    a.add("five");
    List<String> b = new ArrayList<>(a);

    assertTrue(DeepEquals.deepEquals(a, b));

    List<Integer> c = new ArrayList<>();
    c.add(1);
    c.add(2);
    c.add(3);
    c.add(4);
    c.add(5);

    assertFalse(DeepEquals.deepEquals(a, c));

    List<Integer> d = new ArrayList<>();
    d.add(4);
    d.add(6);

    assertFalse(DeepEquals.deepEquals(c, d));

    List<Class1> x1 = new ArrayList<>();
    x1.add(new Class1(true, log(pow(E, 2)), 6));
    x1.add(new Class1(true, tan(PI / 4), 1));
    List<Class1> x2 = new ArrayList<>();
    x2.add(new Class1(true, 2, 6));
    x2.add(new Class1(true, 1, 1));
    assertTrue(DeepEquals.deepEquals(x1, x2));
  }

  @Test
  public void testUnorderedCollection() {
    Set<String> a = new HashSet<>();
    a.add("one");
    a.add("two");
    a.add("three");
    a.add("four");
    a.add("five");
    Set<String> b = new HashSet<>();
    b.add("three");
    b.add("five");
    b.add("one");
    b.add("four");
    b.add("two");
    assertTrue(DeepEquals.deepEquals(a, b));

    Set<Integer> c = new HashSet<>();
    c.add(1);
    c.add(2);
    c.add(3);
    c.add(4);
    c.add(5);
    assertFalse(DeepEquals.deepEquals(a, c));

    Set<Integer> d = new HashSet<>();
    d.add(4);
    d.add(2);
    d.add(6);
    assertFalse(DeepEquals.deepEquals(c, d));

    Set<Class1> x1 = new HashSet<>();
    x1.add(new Class1(true, log(pow(E, 2)), 6));
    x1.add(new Class1(true, tan(PI / 4), 1));
    Set<Class1> x2 = new HashSet<>();
    x2.add(new Class1(true, 2, 6));
    x2.add(new Class1(true, 1, 1));
    assertTrue(DeepEquals.deepEquals(x1, x2));
  }

  @Test
  public void testEquivalentMaps() {
    Map<String, Integer> map1 = new LinkedHashMap<>();
    fillMap(map1);
    Map<String, Integer> map2 = new HashMap<>();
    fillMap(map2);
    assertTrue(DeepEquals.deepEquals(map1, map2));
    assertEquals(DeepEquals.deepHashCode(map1), DeepEquals.deepHashCode(map2));

    map1 = new TreeMap<>();
    fillMap(map1);
    map2 = new TreeMap<>();
    map2 = Collections.synchronizedSortedMap((SortedMap<String, Integer>) map2);
    fillMap(map2);
    assertTrue(DeepEquals.deepEquals(map1, map2));
    assertEquals(DeepEquals.deepHashCode(map1), DeepEquals.deepHashCode(map2));
  }

  @Test
  public void testInequivalentMaps() {
    Map<String, Integer> map1 = new TreeMap<>();
    fillMap(map1);
    Map<String, Integer> map2 = new HashMap<>();
    fillMap(map2);
    // Sorted versus non-sorted Map
    assertFalse(DeepEquals.deepEquals(map1, map2));

    // Hashcodes are equals because the Maps have same elements
    assertEquals(DeepEquals.deepHashCode(map1), DeepEquals.deepHashCode(map2));

    map2 = new TreeMap<>();
    fillMap(map2);
    map2.remove("kilo");
    assertFalse(DeepEquals.deepEquals(map1, map2));

    // Hashcodes are different because contents of maps are different
    assertNotEquals(DeepEquals.deepHashCode(map1), DeepEquals.deepHashCode(map2));

    // Inequality because ConcurrentSkipListMap is a SortedMap
    map1 = new HashMap<>();
    fillMap(map1);
    map2 = new ConcurrentSkipListMap<>();
    fillMap(map2);
    assertFalse(DeepEquals.deepEquals(map1, map2));

    map1 = new TreeMap<>();
    fillMap(map1);
    map2 = new ConcurrentSkipListMap<>();
    fillMap(map2);
    assertTrue(DeepEquals.deepEquals(map1, map2));
    map2.remove("papa");
    assertFalse(DeepEquals.deepEquals(map1, map2));
  }

  @Test
  public void testEquivalentCollections() {
    // ordered Collection
    Collection<String> col1 = new ArrayList<>();
    fillCollection(col1);
    Collection<String> col2 = new LinkedList<>();
    fillCollection(col2);
    assertTrue(DeepEquals.deepEquals(col1, col2));
    assertEquals(DeepEquals.deepHashCode(col1), DeepEquals.deepHashCode(col2));

    // unordered Collections (Set)
    col1 = new LinkedHashSet<>();
    fillCollection(col1);
    col2 = new HashSet<>();
    fillCollection(col2);
    assertTrue(DeepEquals.deepEquals(col1, col2));
    assertEquals(DeepEquals.deepHashCode(col1), DeepEquals.deepHashCode(col2));

    col1 = new TreeSet<>();
    fillCollection(col1);
    col2 = new TreeSet<>();
    Collections.synchronizedSortedSet((SortedSet) col2);
    fillCollection(col2);
    assertTrue(DeepEquals.deepEquals(col1, col2));
    assertEquals(DeepEquals.deepHashCode(col1), DeepEquals.deepHashCode(col2));
  }

  @Test
  public void testInequivalentCollections() {
    Collection<String> col1 = new TreeSet<>();
    fillCollection(col1);
    Collection<String> col2 = new HashSet<>();
    fillCollection(col2);
    assertFalse(DeepEquals.deepEquals(col1, col2));
    assertEquals(DeepEquals.deepHashCode(col1), DeepEquals.deepHashCode(col2));

    col2 = new TreeSet<>();
    fillCollection(col2);
    col2.remove("lima");
    assertFalse(DeepEquals.deepEquals(col1, col2));
    assertNotEquals(DeepEquals.deepHashCode(col1), DeepEquals.deepHashCode(col2));

    assertFalse(DeepEquals.deepEquals(new HashMap(), new ArrayList()));
    assertFalse(DeepEquals.deepEquals(new ArrayList(), new HashMap()));
  }

  @Test
  public void testArray() {
    Object[] a1 = new Object[]{"alpha", "bravo", "charlie", "delta"};
    Object[] a2 = new Object[]{"alpha", "bravo", "charlie", "delta"};

    assertTrue(DeepEquals.deepEquals(a1, a2));
    assertEquals(DeepEquals.deepHashCode(a1), DeepEquals.deepHashCode(a2));
    a2[3] = "echo";
    assertFalse(DeepEquals.deepEquals(a1, a2));
    assertNotEquals(DeepEquals.deepHashCode(a1), DeepEquals.deepHashCode(a2));
  }

  @Test
  public void testHasCustomMethod() {
    assertFalse(DeepEquals.hasCustomEquals(EmptyClass.class));
    assertFalse(DeepEquals.hasCustomHashCode(Class1.class));

    assertTrue(DeepEquals.hasCustomEquals(EmptyClassWithEquals.class));
    assertTrue(DeepEquals.hasCustomHashCode(EmptyClassWithEquals.class));
  }

  @Test
  public void testSymmetry() {
    boolean one = DeepEquals.deepEquals(new ArrayList<String>(), new EmptyClass());
    boolean two = DeepEquals.deepEquals(new EmptyClass(), new ArrayList<String>());
    assert one == two;
  }

  private static class EmptyClass {

  }

  private static class EmptyClassWithEquals {

    public boolean equals(Object obj) {
      return obj instanceof EmptyClassWithEquals;
    }

    public int hashCode() {
      return 0;
    }
  }

  private static class Class1 {

    private boolean b;
    private double d;
    int i;

    private Class1() {
    }

    private Class1(boolean b, double d, int i) {
      super();
      this.b = b;
      this.d = d;
      this.i = i;
    }

  }

  private static class Class2 {

    private Float f;
    String s;
    short ss;
    Class1 c;

    private Class2(float f, String s, short ss, Class1 c) {
      super();
      this.f = f;
      this.s = s;
      this.ss = ss;
      this.c = c;
    }

    private Class2() {
    }
  }

  private void fillMap(Map<String, Integer> map) {
    map.put("zulu", 26);
    map.put("alpha", 1);
    map.put("bravo", 2);
    map.put("charlie", 3);
    map.put("delta", 4);
    map.put("echo", 5);
    map.put("foxtrot", 6);
    map.put("golf", 7);
    map.put("hotel", 8);
    map.put("india", 9);
    map.put("juliet", 10);
    map.put("kilo", 11);
    map.put("lima", 12);
    map.put("mike", 13);
    map.put("november", 14);
    map.put("oscar", 15);
    map.put("papa", 16);
    map.put("quebec", 17);
    map.put("romeo", 18);
    map.put("sierra", 19);
    map.put("tango", 20);
    map.put("uniform", 21);
    map.put("victor", 22);
    map.put("whiskey", 23);
    map.put("xray", 24);
    map.put("yankee", 25);
  }

  private void fillCollection(Collection<String> col) {
    col.add("zulu");
    col.add("alpha");
    col.add("bravo");
    col.add("charlie");
    col.add("delta");
    col.add("echo");
    col.add("foxtrot");
    col.add("golf");
    col.add("hotel");
    col.add("india");
    col.add("juliet");
    col.add("kilo");
    col.add("lima");
    col.add("mike");
    col.add("november");
    col.add("oscar");
    col.add("papa");
    col.add("quebec");
    col.add("romeo");
    col.add("sierra");
    col.add("tango");
    col.add("uniform");
    col.add("victor");
    col.add("whiskey");
    col.add("xray");
    col.add("yankee");
  }
}