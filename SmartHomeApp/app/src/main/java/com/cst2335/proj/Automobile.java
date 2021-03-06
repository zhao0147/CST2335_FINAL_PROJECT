package com.cst2335.proj;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * class Automobile
 */
public class Automobile extends AppCompatActivity {

    /**database for write*/
    protected SQLiteDatabase dbWrite;
    /**speed*/
    protected TextView tvSpeed;
    /**odometer*/
    protected TextView tvKm;
    /**driving mode*/
    protected int driveMode = P_MODE;
    protected static final int P_MODE = 100;
    protected static final int D_MODE = 101;
    protected static final int R_MODE = 102;
    protected static final int NULL_MODE = 109;
    /**update information or not*/
    protected boolean isUpdate = true;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_automobile);

        //textviews
        tvSpeed = (TextView)findViewById(R.id.textView_speed);
        tvKm = (TextView)findViewById(R.id.textView_km);

        //button about
        Button btnAbout = (Button)findViewById(R.id.button_about);
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Automobile.this);
                String strAbout = "Author: Sulin Zhao, Version: 1.0\n\nThis " +
                        "is an Application which simulates driving a car.You could reach the settings of " +
                        "a car by clicking the settings button. After you get all the settings ready," +
                        "you could change the shift to start driving.";
                builder.setMessage(strAbout);
                builder.setTitle("About");
                builder.create().show();
            }
        });

        //button settings
        Button btnSetting = (Button)findViewById(R.id.button_setting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Automobile.this, AutomobileItemListActivity.class);
                startActivity(intent);
            }
        });

        //radio buttons
        RadioGroup rg = (RadioGroup)findViewById(R.id.radioGroup);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int nTmp = 0;
                String strTmp = "";
                switch (checkedId) {
                    case R.id.radioButton_P:
                        driveMode = P_MODE;
                        tvSpeed.setText("0");
                        break;

                    case R.id.radioButton_D:
                        driveMode = D_MODE;
                        tvSpeed.setText(AutomobileDatabaseOperate.getSpeedForward() + "");
                        break;

                    case R.id.radioButton_R:
                        driveMode = R_MODE;
                        tvSpeed.setText(AutomobileDatabaseOperate.getSpeedBackward() + "");
                        break;
                }
            }
        });

        ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar);
        pb.setVisibility(View.VISIBLE);

        //start asynctask
        DatabaseQuery dbQuery = new DatabaseQuery();
        dbQuery.execute();
        Running run = new Running();
        run.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isUpdate = true;
        updateInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isUpdate = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //exit thread
        driveMode = NULL_MODE;

        //save database (update table)
        AutomobileDatabaseOperate.write();

        //close database
        dbWrite.close();
    }

    /**
     * sleep
     * @param millisec time to sleep in milliseconds
     */
    protected void sleep(int millisec) {
        try {
            Thread.sleep(millisec);
        }
        catch (Exception e) {

        }
    }

    /**
     * update driving information
     */
    protected void updateInfo() {
        int speed = 0;
        if (driveMode == D_MODE) {
            speed = AutomobileDatabaseOperate.getSpeedForward();
        }
        else if (driveMode == R_MODE) {
            speed = AutomobileDatabaseOperate.getSpeedBackward();
        }
        tvSpeed.setText(speed + "");

        final TextView tvKm = (TextView)findViewById(R.id.textView_km);
        tvKm.setText(String.format("%.1f", AutomobileDatabaseOperate.getOdometer()));

        final ProgressBar pbGas = (ProgressBar)findViewById(R.id.progressBar_gas);
        pbGas.setProgress((int)AutomobileDatabaseOperate.getGasLevel() * 100 / AutomobileDatabaseOperate.FULL_FUEL);
    }

    /**
     * class DatabaseQuery, used for reading database
     */
    class DatabaseQuery extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String ...args) {
            try {
                //read database
                AutomobileDatabaseHelper dbHelper = new AutomobileDatabaseHelper(Automobile.this);
                dbWrite = dbHelper.getWritableDatabase();
                AutomobileDatabaseOperate.setDatabaseHandle(dbWrite);

                publishProgress(new String[]{"10"});
                sleep(350);
                publishProgress(new String[]{"40"});

                //read settings from table
                AutomobileDatabaseOperate.read();

                publishProgress(new String[]{"80"});
                sleep(350);
                publishProgress(new String[]{"100"});
            }
            catch (Exception e) {
                return e.getMessage();
            }
            sleep(100);
            return "done";
        }

        @Override
        protected void onProgressUpdate(String ...values) {
            ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar);
            pb.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String result) {

            //update driving info
            updateInfo();

            ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar);
            pb.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * class Running, used to update odometer and fuel
     */
    class Running extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String ...args) {
            int speed = 0;
            while (driveMode != NULL_MODE) {
                switch (driveMode) {
                    case P_MODE:
                        break;
                    case D_MODE:
                        speed = AutomobileDatabaseOperate.getSpeedForward();
                        AutomobileDatabaseOperate.setOdometer(AutomobileDatabaseOperate.getOdometer() + (float)(speed * 1.0/3600));
                        AutomobileDatabaseOperate.setGasLevel(AutomobileDatabaseOperate.getGasLevel() - (float)(speed * 1.0/3600)/10);
                        break;
                    case R_MODE:
                        speed = AutomobileDatabaseOperate.getSpeedBackward();
                        AutomobileDatabaseOperate.setOdometer(AutomobileDatabaseOperate.getOdometer() + (float)(speed * 1.0/3600));
                        AutomobileDatabaseOperate.setGasLevel(AutomobileDatabaseOperate.getGasLevel() - (float)(speed * 1.0/3600)/10);
                        break;
                    default:
                        break;
                }
                sleep(1000);
                if (driveMode != P_MODE && isUpdate) {
                    publishProgress(new String[]{""});
                }
            }
            return "done";
        }

        @Override
        protected void onProgressUpdate(String ...values) {
            updateInfo();


            //check fuel
            float gas = AutomobileDatabaseOperate.getGasLevel();
            if (gas < 0.1) {   //stop
                //set stop
                RadioButton rbP = (RadioButton)findViewById(R.id.radioButton_P);
                RadioButton rbD = (RadioButton)findViewById(R.id.radioButton_D);
                RadioButton rbR = (RadioButton)findViewById(R.id.radioButton_R);
                rbP.setChecked(true);
                rbD.setChecked(false);
                rbR.setChecked(false);
                driveMode = P_MODE;
                tvSpeed.setText("0");

                //snackbar
                Snackbar.make(findViewById(android.R.id.content), "Out of Gas!!!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else if (gas < 1) {  //remind
                Toast toast = Toast.makeText(Automobile.this, "There is no much gas left!!!", Toast.LENGTH_LONG);
                toast.show();
            }
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

}
