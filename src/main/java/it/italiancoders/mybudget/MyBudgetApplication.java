package it.italiancoders.mybudget;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import it.italiancoders.mybudget.dao.user.UserDao;
import it.italiancoders.mybudget.model.api.User;
import it.italiancoders.mybudget.service.account.AccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.apache.commons.lang3.LocaleUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class MyBudgetApplication {

    private static final  String FIREBASE_CONF_PATH = "/firebase/mybudget-5c4c1-firebase-adminsdk-yju1l-fd0b5ea35f.json";
    private static final  Logger logger = LoggerFactory.getLogger(MyBudgetApplication.class);

    @Autowired
    AccountManager accountManager;

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        initFirebase();


    }


    private void initFirebase() {

        try {
            String path = this.getClass().getResource("/static").getFile();
            FileInputStream serviceAccount =
                    new FileInputStream(path + FIREBASE_CONF_PATH);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://mybudget-5c4c1.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            logger.error(" unable to initialize firebase", e);
        }
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

    @Bean
    public Executor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    public static void main(String[] args) {
        SpringApplication.run(MyBudgetApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(AccountManager accountManager) {
        return (args) ->
            accountManager.generateAutoMovement(new Date());

    }

    @Scheduled(cron = "0 0 0 * * *", zone = "GMT")
    public void scheduleAutoMovementGen() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        date = cal.getTime();

        accountManager.generateAutoMovement(date);

    }

}
