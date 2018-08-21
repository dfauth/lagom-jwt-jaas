package thingy

import org.apache.logging.log4j.scala.Logging
import org.scalatest._

class TestSpec extends FlatSpec with Matchers with Logging {

    "A State" should "do something" in {


    }

  /**

  @BeforeTest
  public void setUp() {
        Resource.ROOT.permitsActions("*").byPrincipal("me");
        Resource.ROOT.resource("A").permitsActions("read", "write").byPrincipal("fred")
                         .resource("A1")
                             .resource("A2").permitsActions("read", "write", "execute").byPrincipal("barney", "bambam");
        Resource.ROOT.resource("B").permitsActions("read").byPrincipal("wilma")
                         .resource("B1").permitsActions("poke").byPrincipal("roger");
        Resource.ROOT.resource("C").permitsActions("read").byPrincipal("wilma")
                         .resource("C1")
                             .resource("C2")
                                 .resource("C3").permitsActions("read", "write", "execute").byPrincipal("boris");
    }

  @Test
  public void testIt() {

//        System.setProperty("login.configuration.provider", MyConfiguration.class.getName());
//        logger.info("login.configuration.provider: "+System.getProperty("login.configuration.provider"));
        Configuration.setConfiguration(new MyConfiguration());

        Policy.setPolicy(new Policy() {


  @Override
  public boolean implies(ProtectionDomain domain, Permission permission) {
                if (permission instanceof MyPermission) {
                    MyPermission myPermission = MyPermission.class.cast(permission);
                    return Arrays.asList(domain.getPrincipals()).stream().anyMatch(p -> myPermission.model.test(p));
                }
                return super.implies(domain, permission);
            }
        });

        testIt("A/A1/A2", "read", "trader_role", "barney");
    }

    public void testIt(String resource, String action, String... principals) {
        Subject subject = new Subject();

        LoginContext lc = null;
        try {
            lc = new LoginContext("Sample", subject, new MyCallbackHandler(principals));

            // the user has 3 attempts to authenticate successfully
            int i;
            for (i = 0; i < 3; i++) {
                try {

                    // attempt authentication
                    lc.login();

                    // if we return with no exception,
                    // authentication succeeded
                    break;

                } catch (LoginException e) {
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }

            // did they fail three times?
            if (i == 3) {
                throw new RuntimeException("Sorry");
            }
        } catch (LoginException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        logger.info("Authentication succeeded!");

        Subject.doAs(subject, (PrivilegedAction<Void>) () -> {
            try {
                AccessController.checkPermission(new MyPermission("fred", resource, action));
                return null;
            } catch (AccessControlException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });

    }

    private class MyConfiguration extends Configuration {
  @Override
  public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
            return new AppConfigurationEntry[]{new AppConfigurationEntry(SampleLoginModule.class.getName(), AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, Collections.EMPTY_MAP)};
        }
    }

    private static class MyPermission extends BasicPermission {

        private final String resource;
        private final PermissionModel model;

        public MyPermission(String name) {
            this(name, "*", "*");
        }

        public MyPermission(String name, String resource) {
            this(name, resource, "*");
        }

        public MyPermission(String name, String resource, String action) {
            super(name);
            this.resource = resource;
            this.model = Resource.ROOT.find(resource).withAction(action);
        }

  @Override
  public boolean implies(Permission permission) {
            if(permission instanceof MyPermission) {
                MyPermission p = MyPermission.class.cast(permission);
                return p.implies(this);
            }
            return false;
        }

    }



    */
  }