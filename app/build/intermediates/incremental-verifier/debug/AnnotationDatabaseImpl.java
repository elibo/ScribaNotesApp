
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.google.inject.AnnotationDatabase;
import roboguice.fragment.FragmentUtil;

public class AnnotationDatabaseImpl extends AnnotationDatabase {

    public void fillAnnotationClassesAndFieldsNames(HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToFieldSet) {
    
        String annotationClassName = null;
        Map<String, Set<String>> mapClassWithInjectionNameToFieldSet = null;
        Set<String> fieldNameSet = null;


        annotationClassName = "com.google.inject.Inject";
        mapClassWithInjectionNameToFieldSet = mapAnnotationToMapClassWithInjectionNameToFieldSet.get(annotationClassName);
        if( mapClassWithInjectionNameToFieldSet == null ) {
            mapClassWithInjectionNameToFieldSet = new HashMap<String, Set<String>>();
            mapAnnotationToMapClassWithInjectionNameToFieldSet.put(annotationClassName, mapClassWithInjectionNameToFieldSet);
        }

        fieldNameSet = new HashSet<String>();
        fieldNameSet.add("noteDAO");
        mapClassWithInjectionNameToFieldSet.put("com.materialnotes.activity.MainActivity", fieldNameSet);


        annotationClassName = "roboguice.inject.InjectView";
        mapClassWithInjectionNameToFieldSet = mapAnnotationToMapClassWithInjectionNameToFieldSet.get(annotationClassName);
        if( mapClassWithInjectionNameToFieldSet == null ) {
            mapClassWithInjectionNameToFieldSet = new HashMap<String, Set<String>>();
            mapAnnotationToMapClassWithInjectionNameToFieldSet.put(annotationClassName, mapClassWithInjectionNameToFieldSet);
        }

        fieldNameSet = new HashSet<String>();
        fieldNameSet.add("emptyListTextView");
        fieldNameSet.add("addNoteButton");
        fieldNameSet.add("listView");
        mapClassWithInjectionNameToFieldSet.put("com.materialnotes.activity.MainActivity", fieldNameSet);

        fieldNameSet = new HashSet<String>();
        fieldNameSet.add("noteTitleText");
        fieldNameSet.add("noteContentText");
        mapClassWithInjectionNameToFieldSet.put("com.materialnotes.activity.EditNoteActivity", fieldNameSet);

        fieldNameSet = new HashSet<String>();
        fieldNameSet.add("editNoteButton");
        fieldNameSet.add("noteTitleText");
        fieldNameSet.add("noteCreatedAtDateText");
        fieldNameSet.add("noteUpdatedAtDateText");
        fieldNameSet.add("scrollView");
        fieldNameSet.add("noteContentText");
        mapClassWithInjectionNameToFieldSet.put("com.materialnotes.activity.ViewNoteActivity", fieldNameSet);

    }
    
    public void fillAnnotationClassesAndMethods(HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToMethodsSet) {
    }
    
    public void fillAnnotationClassesAndConstructors(HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToConstructorsSet) {
    }
    
    public void fillClassesContainingInjectionPointSet(HashSet<String> classesContainingInjectionPointsSet) {
        classesContainingInjectionPointsSet.add("com.materialnotes.activity.MainActivity");
        classesContainingInjectionPointsSet.add("com.materialnotes.activity.EditNoteActivity");
        classesContainingInjectionPointsSet.add("com.materialnotes.activity.ViewNoteActivity");
    }
    

    public void fillBindableClasses(HashSet<String> injectedClasses) {
        injectedClasses.add("android.widget.TextView");
        injectedClasses.add("android.widget.ScrollView");
        injectedClasses.add("com.materialnotes.data.dao.NoteDAO");
        injectedClasses.add("android.widget.ListView");
        injectedClasses.add("com.shamanland.fab.FloatingActionButton");
        injectedClasses.add("android.widget.EditText");

        if(FragmentUtil.hasNative) {
            injectedClasses.add("android.app.FragmentManager");
        }

        if(FragmentUtil.hasSupport) {
            injectedClasses.add("android.support.v4.app.FragmentManager");
        }
    }

}
