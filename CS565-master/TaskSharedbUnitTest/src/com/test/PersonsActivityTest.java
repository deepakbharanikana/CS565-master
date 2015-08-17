import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.iubcsse.sharetask.MainActivity;
import com.iubcsse.sharetask.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.clickOn;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class PersonsActivityTest {
    private personActivity personActivity;
    private Button addANewPersonButton;
    private Button personButton;
   

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(PersonActivity.class).create().visible().get();
        personButton = (Button) activity.findViewById(R.id.list_personInfo);
       
    }
    
    @Test
    public void pressingTheButtonShouldStartTheAssignTaskInActivity() throws Exception {
        loginButton.performClick();

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);

        assertThat(shadowIntent.getComponent().getClassName(), equalTo(AssignTaskActivity.class.getName()));
    }

    @Test
    public void shouldHaveAButtonThatSaysAddPerson() throws Exception {
        assertThat((String) addANewPersonButton.getText(), equalTo("Add People"));
        
    }

    @Test
    public void pressingTheButtonShouldStartMainActivity() throws Exception {
    	groupButton.performClick();

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);
        assertThat(shadowIntent.getComponent().getClassName(), equalTo(MainActivity.class.getName()));
    }

    @Test
    public void pressingTheButtonShouldRedirectToPersonsActivity() throws Exception {
        loginButton.performClick();

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);

        assertThat(shadowIntent.getComponent().getClassName(), equalTo(PersonsActivity.class.getName()));
    }

}
