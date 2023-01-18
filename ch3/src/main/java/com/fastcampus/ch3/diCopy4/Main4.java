package com.fastcampus.ch3.diCopy4;

import com.google.common.reflect.ClassPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component class Car {
    @Resource
    Engine engine;
    @Resource
    Door door;

    @Override
    public String toString() {
        return "Car{" +
                "engine=" + engine +
                ", door=" + door +
                '}';
    }
}
@Component class SportsCar extends Car {}
@Component class Truck extends Car {}
@Component class Engine{}
@Component class Door {}
class AppContext {
    Map map;

    AppContext() {
        map = new HashMap();
        doComponentScan();
//        doAutowired();
        doResource();
    }

    private void doResource() {
        try {
            // map에 저장된 iv중 @Resource 붙어있으면
            // map에서 iv 이름에 맞는 객체를 찾아서 연결(객체의 주소를 iv에 저장)
            for (Object bean : map.values()) {
                for (Field fld : bean.getClass().getDeclaredFields()) {
                    if (fld.getAnnotation(Resource.class) != null) { // byName
                        fld.set(bean, getBean(fld.getType())); // car.engine = obj;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doAutowired() {
        try {
            // map에 저장된 iv중 @Autowired 붙어있으면
            // map에서 iv 타입에 맞는 객체를 찾아서 연결(객체의 주소를 iv에 저장)
            for (Object bean : map.values()) {
                for (Field fld : bean.getClass().getDeclaredFields()) {
                    if (fld.getAnnotation(Autowired.class) != null) {
                        fld.set(bean, getBean(fld.getType())); // car.engine = obj;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void doComponentScan() {
        try {
            ClassLoader classLoader = AppContext.class.getClassLoader();
            ClassPath classPath = ClassPath.from(classLoader);
            Set<ClassPath.ClassInfo> set = classPath.getTopLevelClasses("com.fastcampus.ch3.diCopy4");// classPath에서 클래스 목록을 가져오는 작업

            for (ClassPath.ClassInfo classInfo : set) {
                Class clazz = classInfo.load();
                Component component = (Component) clazz.getAnnotation(Component.class);

                if (component != null) {
                    String id = StringUtils.uncapitalize(classInfo.getSimpleName());
                    map.put(id, clazz.newInstance());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Object getBean(String key) {return map.get(key);} // byName으로 객체를 검색
    Object getBean(Class clazz) {
        // 타입으로 찾기
        for (Object obj : map.values()) {
            if (clazz.isInstance(obj)) {
                return obj;
            }
        }
        return null;
    }
}


public class Main4 {
    public static void main(String[] args) {
        AppContext ac = new AppContext();
        Car car = (Car) ac.getBean("car");
        Engine engine = (Engine) ac.getBean("engine");
        Door door = (Door) ac.getBean(Door.class);

//        car.engine = engine;
//        car.door = door;

        System.out.println("car = " + car);
        System.out.println("engine = " + engine);
        System.out.println("door = " + door);

    }
}
